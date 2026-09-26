package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.Repository
import com.example.ui.screens.*
import com.example.ui.theme.EduQuestTheme
import com.example.ui.viewmodel.EduViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "eduquest_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    private val repository by lazy { Repository(db) }
    private lateinit var viewModel: EduViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        viewModel = EduViewModel(repository)

        // Run prepopulation asynchronously on startup
        lifecycleScope.launch {
            repository.prepopulateIfEmpty()
        }

        enableEdgeToEdge()

        setContent {
            EduQuestTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()

                // Globally handle Android system back button
                BackHandler(enabled = currentScreen != Screen.Dashboard) {
                    viewModel.navigateBack()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    val mainModifier = Modifier.padding(innerPadding)
                    
                    when (val screen = currentScreen) {
                        is Screen.Dashboard -> {
                            DashboardScreen(viewModel = viewModel, modifier = mainModifier)
                        }
                        is Screen.StudyMaterial -> {
                            StudyMaterialScreen(viewModel = viewModel, modifier = mainModifier)
                        }
                        is Screen.ChapterDetail -> {
                            ChapterDetailScreen(
                                chapter = screen.chapter,
                                subject = screen.subject,
                                viewModel = viewModel,
                                modifier = mainModifier
                            )
                        }
                        is Screen.QuizList, is Screen.QuizActive, is Screen.QuizResult -> {
                            QuizScreen(viewModel = viewModel, modifier = mainModifier)
                        }
                        is Screen.CurrentAffairs -> {
                            CurrentAffairsScreen(viewModel = viewModel, modifier = mainModifier)
                        }
                        is Screen.AdminPanel -> {
                            AdminScreen(viewModel = viewModel, modifier = mainModifier)
                        }
                    }
                }
            }
        }
    }
}
