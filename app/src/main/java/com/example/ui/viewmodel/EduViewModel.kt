package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Dashboard : Screen()
    data class StudyMaterial(val subject: Subject? = null) : Screen()
    data class ChapterDetail(val chapter: Chapter, val subject: Subject) : Screen()
    object QuizList : Screen()
    data class QuizActive(
        val quiz: Quiz,
        val questions: List<Question>,
        val currentQuestionIndex: Int,
        val selectedAnswers: Map<Int, String>, // QuestionId -> Selected option
        val timeLeftSeconds: Int
    ) : Screen()
    data class QuizResult(
        val quiz: Quiz,
        val score: Int,
        val totalQuestions: Int,
        val questions: List<Question>,
        val selectedAnswers: Map<Int, String>
    ) : Screen()
    object CurrentAffairs : Screen()
    object AdminPanel : Screen()
}

class EduViewModel(private val repository: Repository) : ViewModel() {

    // Main Flows from DB
    val subjects: StateFlow<List<Subject>> = repository.subjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentAffairs: StateFlow<List<CurrentAffair>> = repository.currentAffairs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizHistory: StateFlow<List<QuizHistory>> = repository.quizHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizzes: StateFlow<List<Quiz>> = repository.quizzes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation and Active state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val navigationStack = mutableListOf<Screen>()

    // Current selected subject's chapters
    private val _currentChapters = MutableStateFlow<List<Chapter>>(emptyList())
    val currentChapters: StateFlow<List<Chapter>> = _currentChapters.asStateFlow()

    // Timer and active quiz job
    private var quizTimerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    fun navigateTo(screen: Screen, addToBackStack: Boolean = true) {
        if (addToBackStack) {
            navigationStack.add(_currentScreen.value)
        }
        _currentScreen.value = screen

        // If navigating to study materials with a specific subject, load chapters
        if (screen is Screen.StudyMaterial && screen.subject != null) {
            loadChapters(screen.subject.id)
        }
    }

    fun navigateBack(): Boolean {
        if (navigationStack.isNotEmpty()) {
            val prev = navigationStack.removeAt(navigationStack.size - 1)
            // Cancel active quiz timer if leaving quiz
            if (_currentScreen.value is Screen.QuizActive && prev !is Screen.QuizActive) {
                quizTimerJob?.cancel()
            }
            _currentScreen.value = prev
            
            // Reload chapters if back to study materials with subject
            if (prev is Screen.StudyMaterial && prev.subject != null) {
                loadChapters(prev.subject.id)
            }
            return true
        }
        return false
    }

    private fun loadChapters(subjectId: Int) {
        viewModelScope.launch {
            repository.getChaptersBySubject(subjectId).collect {
                _currentChapters.value = it
            }
        }
    }

    // Toggle Bookmarks and downloads
    fun toggleChapterBookmark(chapter: Chapter, subject: Subject) {
        viewModelScope.launch {
            val updated = chapter.copy(isBookmarked = !chapter.isBookmarked)
            repository.updateChapter(updated)
            // Refresh detail if open
            val current = _currentScreen.value
            if (current is Screen.ChapterDetail && current.chapter.id == chapter.id) {
                _currentScreen.value = Screen.ChapterDetail(updated, subject)
            }
            // Refresh chapters list
            loadChapters(subject.id)
        }
    }

    fun downloadChapter(chapter: Chapter, subject: Subject) {
        viewModelScope.launch {
            // Simulate downloading animation progress in UI if wanted, but write state immediately
            val updated = chapter.copy(isDownloaded = true)
            repository.updateChapter(updated)
            val current = _currentScreen.value
            if (current is Screen.ChapterDetail && current.chapter.id == chapter.id) {
                _currentScreen.value = Screen.ChapterDetail(updated, subject)
            }
            loadChapters(subject.id)
        }
    }

    fun toggleCurrentAffairBookmark(affair: CurrentAffair) {
        viewModelScope.launch {
            repository.updateCurrentAffair(affair.copy(isBookmarked = !affair.isBookmarked))
        }
    }

    // Quiz Operations
    fun startQuiz(quiz: Quiz) {
        viewModelScope.launch {
            val questions = repository.getQuestionsForQuiz(quiz.id)
            val activeScreen = Screen.QuizActive(
                quiz = quiz,
                questions = questions,
                currentQuestionIndex = 0,
                selectedAnswers = emptyMap(),
                timeLeftSeconds = quiz.durationMinutes * 60
            )
            navigateTo(activeScreen)
            startQuizTimer()
        }
    }

    private fun startQuizTimer() {
        quizTimerJob?.cancel()
        quizTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _currentScreen.value
                if (current is Screen.QuizActive) {
                    if (current.timeLeftSeconds > 1) {
                        _currentScreen.value = current.copy(
                            timeLeftSeconds = current.timeLeftSeconds - 1
                        )
                    } else {
                        // Time out! Submit automatically
                        submitQuiz(current.quiz, current.questions, current.selectedAnswers)
                        break
                    }
                } else {
                    break
                }
            }
        }
    }

    fun selectQuizAnswer(questionId: Int, option: String) {
        val current = _currentScreen.value
        if (current is Screen.QuizActive) {
            val updatedAnswers = current.selectedAnswers.toMutableMap().apply {
                put(questionId, option)
            }
            _currentScreen.value = current.copy(selectedAnswers = updatedAnswers)
        }
    }

    fun nextQuizQuestion() {
        val current = _currentScreen.value
        if (current is Screen.QuizActive) {
            if (current.currentQuestionIndex < current.questions.size - 1) {
                _currentScreen.value = current.copy(
                    currentQuestionIndex = current.currentQuestionIndex + 1
                )
            }
        }
    }

    fun prevQuizQuestion() {
        val current = _currentScreen.value
        if (current is Screen.QuizActive) {
            if (current.currentQuestionIndex > 0) {
                _currentScreen.value = current.copy(
                    currentQuestionIndex = current.currentQuestionIndex - 1
                )
            }
        }
    }

    fun submitQuiz(quiz: Quiz, questions: List<Question>, selectedAnswers: Map<Int, String>) {
        quizTimerJob?.cancel()
        var correctCount = 0
        questions.forEach { q ->
            val selected = selectedAnswers[q.id]
            if (selected == q.correctOption) {
                correctCount++
            }
        }

        viewModelScope.launch {
            val percentage = (correctCount.toFloat() / questions.size) * 100
            val history = QuizHistory(
                quizTitle = quiz.title,
                score = correctCount,
                totalQuestions = questions.size,
                percentage = percentage
            )
            repository.insertQuizHistory(history)
            _currentScreen.value = Screen.QuizResult(
                quiz = quiz,
                score = correctCount,
                totalQuestions = questions.size,
                questions = questions,
                selectedAnswers = selectedAnswers
            )
        }
    }

    // Admin Panel Actions (Simulating external changes)
    fun addCurrentAffairFromAdmin(title: String, category: String, content: String, date: String) {
        viewModelScope.launch {
            val affair = CurrentAffair(
                title = title,
                category = category,
                content = content,
                date = date
            )
            repository.addCurrentAffair(affair)
        }
    }

    fun addChapterFromAdmin(subjectId: Int, title: String, notesText: String, readingTime: String) {
        viewModelScope.launch {
            val chapter = Chapter(
                subjectId = subjectId,
                title = title,
                durationText = readingTime,
                notesText = notesText
            )
            repository.addChapter(chapter)
            // Reload if current subject
            val current = _currentScreen.value
            if (current is Screen.StudyMaterial && current.subject?.id == subjectId) {
                loadChapters(subjectId)
            }
        }
    }

    fun deleteCurrentAffairFromAdmin(id: Int) {
        viewModelScope.launch {
            repository.deleteCurrentAffair(id)
        }
    }

    fun clearQuizHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
