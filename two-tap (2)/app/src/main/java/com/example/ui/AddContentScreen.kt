package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class SaveState {
    object Input : SaveState()
    data class Analyzing(val step: String) : SaveState()
    data class Preview(
        val url: String, 
        val type: String, 
        val originalNotes: String, 
        val analysis: com.example.network.FullAiAnalysis
    ) : SaveState()
    data class Success(val url: String, val title: String) : SaveState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContentScreen(
    viewModel: AppViewModel,
    initialUrl: String? = null,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var url by remember { mutableStateOf(initialUrl ?: "") }
    var notes by remember { mutableStateOf("") }
    var currentSaveState by remember { mutableStateOf<SaveState>(SaveState.Input) }
    var showDuplicateDialog by remember { mutableStateOf(false) }
    var duplicateUrl by remember { mutableStateOf("") }
    
    // Auto-trigger if initialUrl is supplied via share sheet
    LaunchedEffect(initialUrl) {
        if (!initialUrl.isNullOrBlank()) {
            val duplicate = viewModel.findDuplicateContent(initialUrl)
            if (duplicate != null) {
                duplicateUrl = initialUrl
                showDuplicateDialog = true
            } else {
                startAnalysis(initialUrl, notes, viewModel) { state ->
                    currentSaveState = state
                }
            }
        }
    }

    // Smart Duplicate Dialog
    if (showDuplicateDialog) {
        AlertDialog(
            onDismissRequest = { showDuplicateDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Already Saved", fontWeight = FontWeight.Bold) },
            text = { Text("You've already saved this link in TWO TAP. Would you like to save it again or go back?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDuplicateDialog = false
                        startAnalysis(duplicateUrl, notes, viewModel) { state ->
                            currentSaveState = state
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save Anyway")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDuplicateDialog = false
                        onBack()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    when (val state = currentSaveState) {
        is SaveState.Input -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Save to TWO TAP", fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(padding)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Sharing Guide Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "How to Save Content",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "TWO TAP is designed for zero-effort saving directly from other apps like YouTube or TikTok.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Steps layout
                            val steps = listOf(
                                "1. Open YouTube, TikTok, or another social app.",
                                "2. Tap the \"Share\" button on any video or post.",
                                "3. Choose \"TWO TAP\" from the sharing list.",
                                "4. Our AI instantly summarizes and saves it for you!"
                            )
                            steps.forEach { step ->
                                Text(
                                    text = step,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Or, Save a Link Manually",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        placeholder = { Text("Paste any link here...") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = "Link Icon") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("url_input"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("Add optional notes...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = {
                            val duplicate = viewModel.findDuplicateContent(url)
                            if (duplicate != null) {
                                duplicateUrl = url
                                showDuplicateDialog = true
                            } else {
                                startAnalysis(url, notes, viewModel) { state ->
                                    currentSaveState = state
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("analyze_button"),
                        shape = RoundedCornerShape(16.dp),
                        enabled = url.isNotBlank()
                    ) {
                        Text("Analyze & Save", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
        
        is SaveState.Analyzing -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        strokeWidth = 6.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Analyzing Content",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.step,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        is SaveState.Preview -> {
            var editableTitle by remember { mutableStateOf(state.analysis.title) }
            var editableCategory by remember { mutableStateOf(state.analysis.category) }
            var editableTimeMinutes by remember { mutableStateOf(state.analysis.estimatedMinutes.toString()) }
            
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("AI Save Preview", fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { currentSaveState = SaveState.Input }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(padding)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Thumbnail Card
                    val platform = viewModel.getYoutubeThumbnail(state.url).let { yt ->
                        if (yt.isNotEmpty()) "YouTube" else "Web"
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = platform.uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.analysis.author,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Title Input (Editable)
                    Text("Title", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editableTitle,
                        onValueChange = { editableTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text("Category", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = editableCategory,
                                onValueChange = { editableCategory = it },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(0.8f)) {
                            Text("Est. Min", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = editableTimeMinutes,
                                onValueChange = { editableTimeMinutes = it },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Suggested Learning Mode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = state.analysis.suggestedLearningMode,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 13.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("AI Quick Summary", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.analysis.quickSummary,
                            modifier = Modifier.padding(16.dp),
                            lineHeight = 20.sp,
                            fontSize = 14.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { currentSaveState = SaveState.Input },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("preview_cancel_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = {
                                val updatedAnalysis = state.analysis.copy(
                                    title = editableTitle,
                                    category = editableCategory,
                                    estimatedMinutes = editableTimeMinutes.toIntOrNull() ?: state.analysis.estimatedMinutes
                                )
                                viewModel.saveContentWithAnalysis(
                                    url = state.url,
                                    type = state.type,
                                    notes = state.originalNotes,
                                    analysis = updatedAnalysis
                                )
                                currentSaveState = SaveState.Success(state.url, editableTitle)
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .height(56.dp)
                                .testTag("preview_save_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Confirm & Save", fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
        
        is SaveState.Success -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "✅ Saved to TWO TAP",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Our AI is organizing your content.",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Button(
                        onClick = onNavigateHome,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(56.dp)
                            .testTag("success_view_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("View Content", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = {
                            url = ""
                            notes = ""
                            currentSaveState = SaveState.Input
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Save Another", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    TextButton(
                        onClick = onNavigateHome,
                        modifier = Modifier.testTag("success_home_button")
                    ) {
                        Text("Return Home", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

private fun startAnalysis(
    url: String,
    notes: String,
    viewModel: AppViewModel,
    onStateChange: (SaveState) -> Unit
) {
    val coroutineScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
    coroutineScope.launch {
        onStateChange(SaveState.Analyzing("Validating URL and detecting platform..."))
        delay(800)
        
        val detected = viewModel.run {
            val d = com.example.network.GeminiHelper().detectPlatform(url)
            d
        }
        val platform = detected.first
        val type = detected.second
        
        onStateChange(SaveState.Analyzing("Detecting platform ($platform) & extracting metadata..."))
        delay(800)
        
        onStateChange(SaveState.Analyzing("Analyzing key concepts with Gemini AI..."))
        try {
            val analysis = viewModel.runAiAnalysis(url, "", notes)
            onStateChange(SaveState.Preview(url, type, notes, analysis))
        } catch (e: Exception) {
            // Fallback preview
            val fallback = com.example.network.FullAiAnalysis(
                title = "Interesting Saved $platform",
                description = "Knowledge curated on $platform.",
                author = "Author",
                language = "English",
                category = "Technology",
                difficulty = "Intermediate",
                estimatedMinutes = 5,
                quickSummary = "Brief summary pending AI analysis.",
                detailedSummary = "Detailed summary will be generated shortly.",
                keyTakeaways = "• Key takeaways will load.",
                actionSteps = "• Action steps will load.",
                keywords = "curation",
                detectedTopics = "Uncategorized",
                suggestedLearningMode = "Read",
                recommendationScore = 80
            )
            onStateChange(SaveState.Preview(url, type, notes, fallback))
        }
    }
}
