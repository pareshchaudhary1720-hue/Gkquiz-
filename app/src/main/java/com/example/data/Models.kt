package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_affairs")
data class CurrentAffair(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // e.g. "Science & Tech", "Economy", "Polity", "Global"
    val content: String,
    val date: String, // e.g. "Sep 26, 2026"
    val isBookmarked: Boolean = false
)

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey val id: Int,
    val name: String,
    val iconName: String, // e.g. "school", "science", "history", "gavel"
    val description: String
)

@Entity(tableName = "chapters")
data class Chapter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val title: String,
    val durationText: String, // e.g. "15 mins read"
    val notesText: String,
    val isBookmarked: Boolean = false,
    val isDownloaded: Boolean = false
)

@Entity(tableName = "quizzes")
data class Quiz(
    @PrimaryKey val id: Int,
    val title: String,
    val subject: String,
    val durationMinutes: Int,
    val questionCount: Int
)

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey val id: Int,
    val quizId: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String, // "A", "B", "C", "D"
    val explanation: String
)

@Entity(tableName = "quiz_history")
data class QuizHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Float,
    val timestamp: Long = System.currentTimeMillis()
)
