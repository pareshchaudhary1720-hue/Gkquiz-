package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentAffairDao {
    @Query("SELECT * FROM current_affairs ORDER BY id DESC")
    fun getAllCurrentAffairs(): Flow<List<CurrentAffair>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentAffair(affair: CurrentAffair)

    @Update
    suspend fun updateCurrentAffair(affair: CurrentAffair)

    @Query("DELETE FROM current_affairs WHERE id = :id")
    suspend fun deleteCurrentAffair(id: Int)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY id ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)
}

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY id ASC")
    fun getChaptersBySubject(subjectId: Int): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE isBookmarked = 1")
    fun getBookmarkedChapters(): Flow<List<Chapter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter)

    @Update
    suspend fun updateChapter(chapter: Chapter)
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes ORDER BY id ASC")
    fun getAllQuizzes(): Flow<List<Quiz>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: Quiz)
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE quizId = :quizId ORDER BY id ASC")
    suspend fun getQuestionsForQuiz(quizId: Int): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)
}

@Dao
interface QuizHistoryDao {
    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC")
    fun getQuizHistory(): Flow<List<QuizHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizHistory(history: QuizHistory)

    @Query("DELETE FROM quiz_history")
    suspend fun clearHistory()
}

@Database(
    entities = [
        CurrentAffair::class,
        Subject::class,
        Chapter::class,
        Quiz::class,
        Question::class,
        QuizHistory::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currentAffairDao(): CurrentAffairDao
    abstract fun subjectDao(): SubjectDao
    abstract fun chapterDao(): ChapterDao
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao
    abstract fun quizHistoryDao(): QuizHistoryDao
}
