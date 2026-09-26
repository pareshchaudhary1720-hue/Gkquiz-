package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Question
import com.example.data.Quiz
import com.example.data.QuizHistory
import com.example.ui.viewmodel.EduViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val screenState by viewModel.currentScreen.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()
    val history by viewModel.quizHistory.collectAsState()

    when (val state = screenState) {
        is Screen.QuizList -> {
            // Render list of quizzes and past scores
            QuizListStateView(quizzes = quizzes, history = history, viewModel = viewModel, modifier = modifier)
        }
        is Screen.QuizActive -> {
            // Render active quiz layout with timer, options, etc.
            QuizActiveStateView(state = state, viewModel = viewModel, modifier = modifier)
        }
        is Screen.QuizResult -> {
            // Render instant report card and explanations
            QuizResultStateView(state = state, viewModel = viewModel, modifier = modifier)
        }
        else -> {
            // Fallback
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun QuizListStateView(
    quizzes: List<Quiz>,
    history: List<QuizHistory>,
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("quiz_list_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Dashboard) },
                    modifier = Modifier.testTag("quiz_list_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Practice Test Series",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Sharpen your knowledge with real-time timers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Available Tests",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(quizzes) { quiz ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.startQuiz(quiz) }
                    .testTag("quiz_card_${quiz.id}"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = Color(0xFF10B981)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = quiz.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${quiz.durationMinutes} mins",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${quiz.questionCount} MCQs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.startQuiz(quiz) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("start_quiz_btn_${quiz.id}")
                    ) {
                        Text("Start", color = Color.White)
                    }
                }
            }
        }

        // Past Attempts History
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Attempt History",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (history.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearQuizHistory() }) {
                        Text("Clear All")
                    }
                }
            }
        }

        if (history.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No history recorded yet. Complete a test above!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(history) { attempt ->
                val dateStr = remember {
                    val sdf = SimpleDateFormat("MMM dd, hh:mm a", java.util.Locale.getDefault())
                    sdf.format(Date(attempt.timestamp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = attempt.quizTitle,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Attempted: $dateStr",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${attempt.score}/${attempt.totalQuestions}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (attempt.percentage >= 60f) Color(0xFF10B981) else Color(0xFFEF4444)
                                )
                            )
                            Text(
                                text = "${attempt.percentage.toInt()}% Score",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizActiveStateView(
    state: Screen.QuizActive,
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val currentQuestion = state.questions.getOrNull(state.currentQuestionIndex)
    val totalQuestions = state.questions.size

    val minutes = state.timeLeftSeconds / 60
    val seconds = state.timeLeftSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    // Progress bar calculations
    val progress = if (totalQuestions > 0) {
        (state.currentQuestionIndex + 1).toFloat() / totalQuestions
    } else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("active_quiz_panel")
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Quiz Header Panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = state.quiz.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 200.dp)
                        )
                        Text(
                            text = "Question ${state.currentQuestionIndex + 1} of $totalQuestions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Timer block with countdown badge
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (state.timeLeftSeconds < 30) Color(0xFFFEE2E2) else Color(0xFFECFDF5)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (state.timeLeftSeconds < 30) Color(0xFFEF4444) else Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.timeLeftSeconds < 30) Color(0xFFEF4444) else Color(0xFF10B981)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Indicator
                LinearProgressIndicator(
                    progress = { progress },
                    color = Color(0xFF10B981),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                )
            }
        }

        if (currentQuestion == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No questions loaded. Please complete the quiz.")
            }
        } else {
            // Main Question and Option Card Deck
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // The Question text
                item {
                    Text(
                        text = currentQuestion.questionText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            lineHeight = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // MCQ Options (A, B, C, D)
                val selectedOption = state.selectedAnswers[currentQuestion.id]

                item {
                    OptionCard(
                        label = "A",
                        text = currentQuestion.optionA,
                        isSelected = selectedOption == "A",
                        onClick = { viewModel.selectQuizAnswer(currentQuestion.id, "A") }
                    )
                }
                item {
                    OptionCard(
                        label = "B",
                        text = currentQuestion.optionB,
                        isSelected = selectedOption == "B",
                        onClick = { viewModel.selectQuizAnswer(currentQuestion.id, "B") }
                    )
                }
                item {
                    OptionCard(
                        label = "C",
                        text = currentQuestion.optionC,
                        isSelected = selectedOption == "C",
                        onClick = { viewModel.selectQuizAnswer(currentQuestion.id, "C") }
                    )
                }
                item {
                    OptionCard(
                        label = "D",
                        text = currentQuestion.optionD,
                        isSelected = selectedOption == "D",
                        onClick = { viewModel.selectQuizAnswer(currentQuestion.id, "D") }
                    )
                }
            }

            // Bottom Navigation Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                TextButton(
                    onClick = { viewModel.prevQuizQuestion() },
                    enabled = state.currentQuestionIndex > 0,
                    modifier = Modifier.testTag("quiz_prev_btn")
                ) {
                    Text("Previous")
                }

                // Submit or Next
                if (state.currentQuestionIndex == totalQuestions - 1) {
                    Button(
                        onClick = { viewModel.submitQuiz(state.quiz, state.questions, state.selectedAnswers) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("quiz_submit_btn")
                    ) {
                        Text("Finish Test", color = Color.White)
                    }
                } else {
                    Button(
                        onClick = { viewModel.nextQuizQuestion() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("quiz_next_btn")
                    ) {
                        Text("Next Question")
                    }
                }
            }
        }
    }
}

@Composable
fun OptionCard(
    label: String,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (isSelected) Color(0xFFECFDF5) else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .testTag("quiz_option_${label}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (isSelected) Color(0xFF065F46) else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun QuizResultStateView(
    state: Screen.QuizResult,
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val percentage = (state.score.toFloat() / state.totalQuestions) * 100
    val pass = percentage >= 60f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("quiz_result_view")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Result Score Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (pass) Color(0xFFECFDF5) else Color(0xFFFEE2E2)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (pass) Icons.Default.EmojiEvents else Icons.Default.SentimentVeryDissatisfied,
                        contentDescription = null,
                        tint = if (pass) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (pass) "Test Completed Successfully!" else "Need More Practice!",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (pass) Color(0xFF065F46) else Color(0xFF991B1B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You scored",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (pass) Color(0xFF065F46).copy(alpha = 0.7f) else Color(0xFF991B1B).copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${state.score} / ${state.totalQuestions}",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                        color = if (pass) Color(0xFF047857) else Color(0xFFDC2626)
                    )
                    Text(
                        text = "Percentage: ${percentage.toInt()}%",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (pass) Color(0xFF047857) else Color(0xFFDC2626)
                    )
                }
            }
        }

        // Return button
        item {
            Button(
                onClick = { viewModel.navigateTo(Screen.QuizList) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("result_done_btn")
            ) {
                Text("Back to Test Series", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Explanations Header
        item {
            Text(
                text = "Detailed Solutions & Explanations",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // Individual solutions mapping
        items(state.questions) { q ->
            val userAnswer = state.selectedAnswers[q.id]
            val isCorrect = userAnswer == q.correctOption

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = if (isCorrect) "Correct" else "Incorrect",
                            tint = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "• Your Answer: " + (userAnswer ?: "Unanswered"),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCorrect) Color(0xFF047857) else Color(0xFFDC2626)
                        )
                        Text(
                            text = "• Correct Answer: ${q.correctOption}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF047857)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Explanation:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = q.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
