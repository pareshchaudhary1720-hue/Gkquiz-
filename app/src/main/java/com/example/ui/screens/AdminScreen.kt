package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Subject
import com.example.ui.viewmodel.EduViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    var activeTab by remember { mutableStateOf(0) } // 0: Cloud Architecture, 1: Add Current Affair, 2: Add Study Material

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_panel_view")
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.Dashboard) },
                modifier = Modifier.testTag("admin_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Home"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Admin Control & Concept",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Dynamic content feeds without coding",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        // Tab Row
        PrimaryTabRow(selectedTabIndex = activeTab) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                Text("Architecture", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                Text("Add News", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                Text("Add Chapter", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
            }
        }

        when (activeTab) {
            0 -> ArchitectureTab()
            1 -> AddNewsTab(viewModel)
            2 -> AddChapterTab(viewModel, subjects)
        }
    }
}

@Composable
fun ArchitectureTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        "How Content is Updated Dynamically",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "To update Study Material and Daily Current Affairs daily without resubmitting the app to the Google Play Store, we decoupling the content from the APK compiled code using modern client-server architectures.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                )
            }
        }

        Text(
            text = "Recommended Cloud Integrations",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        IntegrationStrategyCard(
            title = "1. Firebase Firestore & Storage",
            description = "Ideal for professional, production-ready educational apps with instantaneous sync.",
            steps = listOf(
                "Store Current Affairs in a Firestore collection: 'current_affairs'.",
                "Store Study PDFs/Notes in Firebase Storage and save download URLs in a collection: 'study_material'.",
                "In the Android code, use Retrofit or Firebase SDK with 'addSnapshotListener' to stream updates live directly into the local SQLite Room database, ensuring full offline functionality."
            ),
            color = Color(0xFFFF9100)
        )

        IntegrationStrategyCard(
            title = "2. Google Sheets API (Free & Quick)",
            description = "Easiest approach for teachers or small institutes to update everything via a spreadsheet.",
            steps = listOf(
                "Create a Google Spreadsheet with tabs: 'Current Affairs', 'Study Materials'.",
                "Write a free Google Apps Script that triggers onEdit and publishes a secure web app JSON API endpoint.",
                "Configure Retrofit in the Android app to make a single GET call to this script on startup, downloading new data and inserting it into Room."
            ),
            color = Color(0xFF0F9D58)
        )

        IntegrationStrategyCard(
            title = "3. Headless CMS / Custom API Dashboard",
            description = "Best for scaling platforms with strict admin authorization roles.",
            steps = listOf(
                "Deploy a headless CMS (e.g. Strapi, Contentful) on cloud servers.",
                "Build a small React/Node.js dashboard for administrators.",
                "Configure Android client to poll the rest endpoints periodically, keeping local Room content synced."
            ),
            color = Color(0xFF1E88E5)
        )
    }
}

@Composable
fun IntegrationStrategyCard(
    title: String,
    description: String,
    steps: List<String>,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Divider()
            Spacer(modifier = Modifier.height(12.dp))
            steps.forEachIndexed { idx, step ->
                Row(
                    modifier = Modifier.padding(bottom = 6.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = color
                        )
                    }
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun AddNewsTab(viewModel: EduViewModel) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Science & Tech") }
    var content by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val categories = listOf("Science & Tech", "Economy", "Polity", "Environment", "Global")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mock Admin Deck: Push Live News",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("News Title") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("admin_news_title")
        )

        // Dropdown replacement / Row chips for Category
        Column {
            Text("Category:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.take(3).forEach { cat ->
                    val isSelected = category == cat
                    InputChip(
                        selected = isSelected,
                        onClick = { category = cat },
                        label = { Text(cat) }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.drop(3).forEach { cat ->
                    val isSelected = category == cat
                    InputChip(
                        selected = isSelected,
                        onClick = { category = cat },
                        label = { Text(cat) }
                    )
                }
            }
        }

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("News Article Content") },
            shape = RoundedCornerShape(12.dp),
            minLines = 4,
            modifier = Modifier.fillMaxWidth().testTag("admin_news_content")
        )

        Button(
            onClick = {
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val dateToday = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
                    viewModel.addCurrentAffairFromAdmin(title, category, content, dateToday)
                    title = ""
                    content = ""
                    successMessage = "Published Live! Go check the Current Affairs tab."
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("publish_news_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
        ) {
            Text("Publish Live to Home Feed", color = Color.White)
        }

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = Color(0xFF10B981),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AddChapterTab(viewModel: EduViewModel, subjects: List<Subject>) {
    var selectedSubjectId by remember { mutableStateOf(1) }
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var readingTime by remember { mutableStateOf("10 mins read") }
    var successMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mock Admin Deck: Add Study Chapter Notes",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Select Subject Chips
        Column {
            Text("Select Target Subject:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(4.dp))
            subjects.forEach { sub ->
                val isSelected = selectedSubjectId == sub.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedSubjectId = sub.id },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = isSelected, onClick = { selectedSubjectId = sub.id })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(sub.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Chapter Title (e.g. Chapter 4: President)") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("admin_chapter_title")
        )

        OutlinedTextField(
            value = readingTime,
            onValueChange = { readingTime = it },
            label = { Text("Estimated reading duration (e.g. 10 mins read)") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Chapter Notes / Text (Use **Header** for sections)") },
            placeholder = { Text("Example:\n**Key Concept**\nDescribe the details here.\n\n• Point 1\n• Point 2") },
            shape = RoundedCornerShape(12.dp),
            minLines = 6,
            modifier = Modifier.fillMaxWidth().testTag("admin_chapter_notes")
        )

        Button(
            onClick = {
                if (title.isNotEmpty() && notes.isNotEmpty()) {
                    viewModel.addChapterFromAdmin(selectedSubjectId, title, notes, readingTime)
                    title = ""
                    notes = ""
                    successMessage = "Chapter Added Successfully! Visit Study Notes tab to view."
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("publish_chapter_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Add to SQLite Database Cache", color = Color.White)
        }

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
