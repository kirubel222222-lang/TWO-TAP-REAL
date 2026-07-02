package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ------------------------------------------------------------------------
// PREMIUM ONBOARDING COLOR THEME
// ------------------------------------------------------------------------
val SpaceDark = Color(0xFF0A0B10)
val DeepSlate = Color(0xFF121320)
val NeonPurple = Color(0xFF9B5DE5)
val NeonPink = Color(0xFFF15BB5)
val NeonTeal = Color(0xFF00F5D4)

data class OnboardingStep(
    val titlePrefix: String,
    val highlightWord: String,
    val titleSuffix: String = "",
    val description: String,
    val illustration: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var currentStep by remember { mutableStateOf(0) }

    val steps = listOf(
        OnboardingStep(
            titlePrefix = "Welcome to\n",
            highlightWord = "Problem Discovery",
            description = "Discover real problems.\nBuild real solutions.",
            illustration = { WelcomeIllustration() }
        ),
        OnboardingStep(
            titlePrefix = "Find ",
            highlightWord = "Problems",
            titleSuffix = "\nThat Matter",
            description = "Explore real-world problems\nshared by people like you.",
            illustration = { MagnifyingGlassIllustration() }
        ),
        OnboardingStep(
            titlePrefix = "Validate with\n",
            highlightWord = "Community",
            description = "Upvote, comment, and discuss\nto validate problem demand.",
            illustration = { CommunityIllustration() }
        ),
        OnboardingStep(
            titlePrefix = "",
            highlightWord = "AI Finds",
            titleSuffix = "\nOpportunities",
            description = "Our AI analyzes and ranks\nproblems with high potential.",
            illustration = { AiOpportunitiesIllustration() }
        ),
        OnboardingStep(
            titlePrefix = "Turn Problems into\n",
            highlightWord = "Real Impact",
            description = "Build solutions. Create startups.\nChange the world.",
            illustration = { RealImpactIllustration() }
        )
    )

    if (currentStep < steps.size) {
        val step = steps[currentStep]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceDark)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header Indicators (Label e.g. "1 / 5" + Progress Segments)
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentStep + 1} / ${steps.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(steps.size) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (index <= currentStep) NeonPurple else Color(0xFF232430)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.5f))

                // Display Bold Dual-Tone Title
                val annotatedTitle = buildAnnotatedString {
                    append(step.titlePrefix)
                    withStyle(style = SpanStyle(color = NeonPurple, fontWeight = FontWeight.Bold)) {
                        append(step.highlightWord)
                    }
                    append(step.titleSuffix)
                }
                Text(
                    text = annotatedTitle,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.weight(0.8f))

                // Illustration Box
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    step.illustration()
                }

                Spacer(modifier = Modifier.weight(0.8f))

                // Description Subtitle
                Text(
                    text = step.description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.weight(0.5f))

                // Dot Page Indicators at Bottom
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(steps.size) { index ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentStep) NeonPurple else Color(0xFF232430)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Next Button
                Button(
                    onClick = { currentStep++ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text(
                        text = if (currentStep < steps.size - 1) "Next" else "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                // If last step, show footer branding badge
                if (currentStep == steps.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Adjust,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Problem Discovery",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Let's build the future together!",
                            fontSize = 11.sp,
                            color = NeonPurple
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    } else if (currentStep == steps.size) {
        // Interests Selection Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceDark)
                .padding(24.dp)
        ) {
            Column {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "Choose Your Interests",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select 3 or more topics to get personalized recommendations.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))

                val interests = listOf("Technology", "AI", "Programming", "Business", "Startups", "Design", "Finance", "Marketing", "Science", "Productivity", "Self Improvement", "Fitness")
                var selectedInterests by remember { mutableStateOf(setOf<String>()) }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    val rows = interests.chunked(2)
                    items(rows) { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowItems.forEach { interest ->
                                val isSelected = selectedInterests.contains(interest)
                                Surface(
                                    color = if (isSelected) NeonPurple else DeepSlate,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, if (isSelected) NeonPurple else Color(0xFF232430)),
                                    onClick = {
                                        if (isSelected) {
                                            selectedInterests = selectedInterests - interest
                                        } else if (selectedInterests.size < 10) {
                                            selectedInterests = selectedInterests + interest
                                        }
                                    }
                                ) {
                                    Text(
                                        text = interest,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { currentStep++ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    enabled = selectedInterests.size >= 3
                ) {
                    Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    } else if (currentStep == steps.size + 1) {
        // Learning Goals Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceDark)
                .padding(24.dp)
        ) {
            Column {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "What's Your Goal?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select what you want to achieve with TWO TAP.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))

                val goals = listOf("Learn New Skills", "Grow My Career", "Build Better Habits", "Stay Informed", "Become More Productive", "Start A Business")
                var selectedGoal by remember { mutableStateOf("") }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(goals) { goal ->
                        val isSelected = selectedGoal == goal
                        Surface(
                            color = if (isSelected) NeonPurple.copy(alpha = 0.15f) else DeepSlate,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { selectedGoal = goal },
                            border = BorderStroke(1.5.dp, if (isSelected) NeonPurple else Color(0xFF232430))
                        ) {
                            Text(
                                text = goal,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                                color = if (isSelected) NeonPurple else Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = { currentStep++ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    enabled = selectedGoal.isNotBlank()
                ) {
                    Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    } else if (currentStep == steps.size + 2) {
        // Profile Setup Screen
        var username by remember { mutableStateOf("") }
        var bio by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceDark)
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "Set Up Profile",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add some personal touches to your account.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(48.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(DeepSlate, CircleShape)
                            .border(1.5.dp, NeonPurple, CircleShape)
                            .clickable { /* Choose photo */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Upload Photo",
                            tint = Color.LightGray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = Color(0xFF232430),
                        focusedLabelColor = NeonPurple,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio (Optional)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = Color(0xFF232430),
                        focusedLabelColor = NeonPurple,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(64.dp))

                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    enabled = username.isNotBlank()
                ) {
                    Text("Finish Setup", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ------------------------------------------------------------------------
// CUSTOM VECTOR ILLUSTRATIONS DRAWN IN JETPACK COMPOSE
// ------------------------------------------------------------------------

@Composable
fun WelcomeIllustration() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPurple.copy(alpha = 0.4f), Color.Transparent),
                    center = center,
                    radius = size.width / 2f
                )
            )
            // Portal ring with dash/stroke
            drawCircle(
                color = NeonPurple,
                radius = size.width * 0.35f,
                style = Stroke(width = 3.dp.toPx())
            )
            // Inner glowing dot ring
            drawCircle(
                color = NeonTeal,
                radius = size.width * 0.28f,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Box(
            modifier = Modifier
                .size(110.dp)
                .shadow(16.dp, CircleShape, spotColor = NeonPurple)
                .background(DeepSlate.copy(alpha = 0.95f), CircleShape)
                .border(2.dp, Brush.linearGradient(listOf(NeonPurple, NeonPink)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(54.dp),
                    tint = NeonPurple
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .background(NeonTeal.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("EXPLORER", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = NeonTeal)
                }
            }
        }

        // Floating particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = NeonPink, radius = 4.dp.toPx(), center = Offset(size.width * 0.2f, size.height * 0.3f))
            drawCircle(color = NeonTeal, radius = 3.dp.toPx(), center = Offset(size.width * 0.8f, size.height * 0.25f))
            drawCircle(color = NeonPurple, radius = 5.dp.toPx(), center = Offset(size.width * 0.75f, size.height * 0.75f))
        }
    }
}

@Composable
fun MagnifyingGlassIllustration() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPink.copy(alpha = 0.3f), Color.Transparent),
                    center = center,
                    radius = size.width * 0.45f
                )
            )
        }

        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(12.dp, CircleShape, spotColor = NeonPink)
                .background(DeepSlate.copy(alpha = 0.9f), CircleShape)
                .border(3.dp, Brush.linearGradient(listOf(NeonPink, NeonPurple)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = NeonPink
            )
        }

        // Floating problem chat bubbles with exclamation marks
        Box(
            modifier = Modifier
                .offset(x = (-60).dp, y = (-40).dp)
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .background(DeepSlate, RoundedCornerShape(12.dp))
                .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(NeonPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("UX Issue", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        Box(
            modifier = Modifier
                .offset(x = 60.dp, y = 50.dp)
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .background(DeepSlate, RoundedCornerShape(12.dp))
                .border(1.dp, NeonTeal.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(NeonTeal, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Bugs", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun CommunityIllustration() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonTeal.copy(alpha = 0.3f), Color.Transparent),
                    center = center,
                    radius = size.width * 0.45f
                )
            )
        }

        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(16.dp, CircleShape, spotColor = NeonTeal)
                .background(DeepSlate.copy(alpha = 0.9f), CircleShape)
                .border(2.dp, NeonTeal, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = NeonTeal
            )
        }

        // Floating Upvotes Badge
        Box(
            modifier = Modifier
                .offset(x = 65.dp, y = (-45).dp)
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .background(DeepSlate, RoundedCornerShape(16.dp))
                .border(1.dp, NeonTeal.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = NeonTeal,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("1.2K Upvotes", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // Floating Comments Badge
        Box(
            modifier = Modifier
                .offset(x = (-65).dp, y = 45.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .background(DeepSlate, RoundedCornerShape(16.dp))
                .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Comment,
                    contentDescription = null,
                    tint = NeonPurple,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("128 Comments", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AiOpportunitiesIllustration() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPurple.copy(alpha = 0.3f), Color.Transparent),
                    center = center,
                    radius = size.width * 0.45f
                )
            )
        }

        // Companion Robot Droid
        Box(
            modifier = Modifier
                .offset(x = (-55).dp, y = (-45).dp)
                .size(70.dp)
                .shadow(10.dp, CircleShape, spotColor = NeonPurple)
                .background(DeepSlate, CircleShape)
                .border(2.dp, NeonPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = NeonPurple
                )
                Text("AI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonPurple)
            }
        }

        // Glassmorphic Opportunity Score Card
        Card(
            modifier = Modifier
                .offset(x = 25.dp, y = 25.dp)
                .width(160.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DeepSlate.copy(alpha = 0.95f)),
            border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("Opportunity Score", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("92", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("/100", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 3.dp))
                }

                // Rising Line Chart Visual
                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp)
                    .padding(vertical = 2.dp)) {
                    val path = Path().apply {
                        moveTo(0f, size.height)
                        quadraticTo(size.width * 0.3f, size.height * 0.7f, size.width * 0.6f, size.height * 0.2f)
                        lineTo(size.width, size.height * 0.1f)
                    }
                    drawPath(
                        path = path,
                        color = NeonPurple,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Market:", fontSize = 8.sp, color = Color.LightGray)
                    Text("High", fontSize = 8.sp, color = NeonTeal, fontWeight = FontWeight.Bold)
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Demand:", fontSize = 8.sp, color = Color.LightGray)
                    Text("Very High", fontSize = 8.sp, color = NeonTeal, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RealImpactIllustration() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPink.copy(alpha = 0.3f), Color.Transparent),
                    center = center,
                    radius = size.width * 0.45f
                )
            )
        }

        // Bullseye Target Ring
        Box(
            modifier = Modifier
                .size(130.dp)
                .shadow(16.dp, CircleShape, spotColor = NeonPink)
                .background(DeepSlate.copy(alpha = 0.8f), CircleShape)
                .border(2.dp, NeonPink, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .border(2.dp, NeonPink.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(NeonPink.copy(alpha = 0.15f), CircleShape)
                        .border(2.dp, NeonPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(NeonTeal, CircleShape)
                    )
                }
            }
        }

        // Launch Rocket Overlay
        Box(
            modifier = Modifier
                .offset(x = 45.dp, y = (-45).dp)
                .size(64.dp)
                .shadow(12.dp, CircleShape, spotColor = NeonTeal)
                .background(DeepSlate, CircleShape)
                .border(2.dp, NeonTeal, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = NeonTeal
            )
        }
    }
}
