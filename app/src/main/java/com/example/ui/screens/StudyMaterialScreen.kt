package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Chapter
import com.example.data.Subject
import com.example.ui.viewmodel.EduViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyMaterialScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val screenState by viewModel.currentScreen.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val chapters by viewModel.currentChapters.collectAsState()

    // Since StudyMaterial handles both listing subjects and listing chapters inside a subject,
    // let's look at the active subject in the Screen.StudyMaterial state.
    val activeSubject = (screenState as? Screen.StudyMaterial)?.subject

    if (activeSubject == null) {
        // Render Subjects Grid list
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("subjects_view")
        ) {
            Text(
                text = "Select a Subject",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(subjects) { subject ->
                    SubjectListItem(
                        subject = subject,
                        onClick = {
                            viewModel.navigateTo(Screen.StudyMaterial(subject))
                        }
                    )
                }
            }
        }
    } else {
        // Render Chapters List for the selected Subject
        Column(
            modifier = modifier
                .fillMaxSize()
                .testTag("chapters_view")
        ) {
            // Header with Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("chapter_list_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Subjects"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = activeSubject.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${chapters.size} Chapters available",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            if (chapters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "No Chapters available yet.",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Use the Mock Admin panel to add chapters to this subject in real-time!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(chapters) { chapter ->
                        ChapterListItem(
                            chapter = chapter,
                            onBookmarkToggle = {
                                viewModel.toggleChapterBookmark(chapter, activeSubject)
                            },
                            onDownloadClick = {
                                viewModel.downloadChapter(chapter, activeSubject)
                            },
                            onClick = {
                                viewModel.navigateTo(Screen.ChapterDetail(chapter, activeSubject))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectListItem(
    subject: Subject,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("subject_card_${subject.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (subject.iconName) {
                        "gavel" -> Icons.Default.Gavel
                        "science" -> Icons.Default.Science
                        "history" -> Icons.Default.History
                        "public" -> Icons.Default.Public
                        else -> Icons.Default.School
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subject.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ChapterListItem(
    chapter: Chapter,
    onBookmarkToggle: () -> Unit,
    onDownloadClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("chapter_card_${chapter.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = chapter.durationText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    if (chapter.isDownloaded) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Downloaded",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Offline ready",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }

            // Quick actions
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!chapter.isDownloaded) {
                    IconButton(
                        onClick = onDownloadClick,
                        modifier = Modifier.testTag("download_chapter_btn_${chapter.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Chapter notes",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier.testTag("bookmark_chapter_btn_${chapter.id}")
                ) {
                    Icon(
                        imageVector = if (chapter.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark notes",
                        tint = if (chapter.isBookmarked) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterDetailScreen(
    chapter: Chapter,
    subject: Subject,
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    var fontSizeMultiplier by remember { mutableStateOf(1.0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("chapter_detail_view")
    ) {
        // App bar
        TopAppBar(
            title = {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("chapter_detail_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Chapters"
                    )
                }
            },
            actions = {
                // Adjust text size action for accessibility
                IconButton(onClick = {
                    fontSizeMultiplier = if (fontSizeMultiplier >= 1.4f) 1.0f else fontSizeMultiplier + 0.2f
                }) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = "Increase Font Size"
                    )
                }
                // Bookmark action
                IconButton(onClick = { viewModel.toggleChapterBookmark(chapter, subject) }) {
                    Icon(
                        imageVector = if (chapter.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (chapter.isBookmarked) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Notes content mimicking modern clean reader
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // PDF simulation info bar
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "PDF Reader Mode",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Page 1 of 1",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            // Actual Text Reading Zone with adjustable size
            SelectionContainer {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Split content by lines to identify headers and bullet points
                    val lines = chapter.notesText.split("\n")
                    for (line in lines) {
                        if (line.startsWith("**") && line.endsWith("**")) {
                            // Sub-header
                            Text(
                                text = line.replace("**", ""),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (18 * fontSizeMultiplier).sp
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                            )
                        } else if (line.startsWith("•") || line.startsWith("1.") || line.startsWith("2.") || line.startsWith("3.") || line.startsWith("4.") || line.startsWith("5.") || line.startsWith("6.")) {
                            // Bullet or numbered point
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = (15 * fontSizeMultiplier).sp,
                                    lineHeight = (22 * fontSizeMultiplier).sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        } else if (line.isNotEmpty()) {
                            // Standard paragraph with rich markdown-like parsing for bold elements
                            AnnotatedParagraph(text = line, multiplier = fontSizeMultiplier)
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun AnnotatedParagraph(text: String, multiplier: Float) {
    // Basic bold markdown parser (e.g., replaces **word** with a Bold style)
    val words = text.split(" ")
    var isBold = false
    val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
        words.forEachIndexed { index, word ->
            var cleanWord = word
            if (word.startsWith("**") || word.contains("**")) {
                val parts = word.split("**")
                parts.forEachIndexed { pIndex, part ->
                    if (pIndex % 2 != 0 || (word.startsWith("**") && pIndex == 1)) {
                        withStyle(style = androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary)) {
                            append(part)
                        }
                    } else {
                        append(part)
                    }
                }
            } else {
                append(word)
            }
            if (index < words.size - 1) append(" ")
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = (15 * multiplier).sp,
            lineHeight = (22 * multiplier).sp
        ),
        color = MaterialTheme.colorScheme.onBackground
    )
}

// Wrapping selection container to prevent build error on non-platform targets
@Composable
fun SelectionContainer(content: @Composable () -> Unit) {
    androidx.compose.foundation.text.selection.SelectionContainer {
        content()
    }
}
