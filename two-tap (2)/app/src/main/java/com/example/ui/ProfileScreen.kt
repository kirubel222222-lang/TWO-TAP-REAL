package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.AuthHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    onNavigateHome: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onLogout: () -> Unit
) {
    val allContent by viewModel.allContent.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateHome,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToLibrary,
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Library") },
                    label = { Text("Library") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToAiAssistant,
                    icon = { Icon(Icons.Default.Search, contentDescription = "AI Chat") },
                    label = { Text("AI Chat") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeaderSection()
            Spacer(modifier = Modifier.height(24.dp))
            LearningOverviewCard(allContent = allContent)
            Spacer(modifier = Modifier.height(24.dp))
            KnowledgeScoreSection(allContent = allContent)
            Spacer(modifier = Modifier.height(24.dp))
            AchievementsSection(allContent = allContent)
            Spacer(modifier = Modifier.height(24.dp))
            InterestsSection(allContent = allContent)
            Spacer(modifier = Modifier.height(24.dp))
            SettingsList(onLogout)
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun ProfileHeaderSection() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val user = AuthHelper.getAuth(context)?.currentUser
    
    val displayName = user?.displayName?.takeIf { it.isNotBlank() } ?: "Learner"
    val email = user?.email ?: "No email linked"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profile Picture",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(displayName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(email, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Member since 2026", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { /* TODO Edit Profile */ },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile")
            }
            OutlinedButton(
                onClick = { /* TODO Share Profile */ },
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share")
            }
        }
    }
}

@Composable
fun LearningOverviewCard(allContent: List<com.example.data.SavedContent>) {
    val savedCount = allContent.size
    val completedCount = allContent.count { it.status == "Completed" }
    val totalMinutes = allContent.filter { it.status == "Completed" }.sumOf { it.estimatedTime }
    val hoursStr = if (totalMinutes < 60) "$totalMinutes Min" else String.format("%.1f Hr", totalMinutes / 60.0)
    
    // Calculate streaks based on distinct saved days
    val distinctDays = allContent.map {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        sdf.format(java.util.Date(it.timestamp))
    }.distinct().size
    val streakStr = "$distinctDays Days"
    val longestStreakStr = "${distinctDays + if (distinctDays > 0) 1 else 0} Days"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Learning Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("Saved", savedCount.toString())
                StatItem("Completed", completedCount.toString())
                StatItem("Hours", hoursStr)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("Current Streak", streakStr)
                StatItem("Longest Streak", longestStreakStr)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun KnowledgeScoreSection(allContent: List<com.example.data.SavedContent>) {
    val savedCount = allContent.size
    val completedCount = allContent.count { it.status == "Completed" }
    val xp = (savedCount * 20) + (completedCount * 100)
    
    val level = (xp / 500) + 1
    val nextLevelXp = level * 500
    val currentLevelBaseXp = (level - 1) * 500
    val progress = if (nextLevelXp == currentLevelBaseXp) 0f else {
        ((xp - currentLevelBaseXp).toFloat() / (nextLevelXp - currentLevelBaseXp).toFloat()).coerceIn(0f, 1f)
    }
    
    val levelTitle = when {
        level < 3 -> "Novice Learner"
        level < 6 -> "Knowledge Seeker"
        level < 10 -> "Intellectual Explorer"
        else -> "Master Scholar"
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Knowledge Score", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text("%,d XP".format(xp), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Level $level", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
            Text(levelTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AchievementsSection(allContent: List<com.example.data.SavedContent>) {
    val savedCount = allContent.size
    val completedCount = allContent.count { it.status == "Completed" }

    val achievements = listOf(
        Triple("First Save", "Save 1 item", savedCount >= 1),
        Triple("Explorer", "Save 10 items", savedCount >= 10),
        Triple("Completer", "Complete 1 item", completedCount >= 1),
        Triple("Finisher", "Complete 5 items", completedCount >= 5),
        Triple("Polymath", "3+ categories", allContent.map { it.category }.distinct().size >= 3)
    )

    Column {
        Text(
            "Achievements",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievements) { (title, desc, isUnlocked) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(100.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = title,
                            tint = if (isUnlocked) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) MaterialTheme.colorScheme.onBackground else Color.Gray,
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = desc,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InterestsSection(allContent: List<com.example.data.SavedContent>) {
    val savedCategories = allContent.map { it.category }.filter { it.isNotBlank() && it != "Uncategorized" }.distinct()
    val interests = if (savedCategories.isEmpty()) listOf("AI", "Business", "Programming", "Startups", "Design") else savedCategories

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Interests", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            TextButton(onClick = { /* TODO */ }) { Text("Edit") }
        }
        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            interests.forEach { interest ->
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(interest, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun SettingsList(onLogout: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text("Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))
        SettingsItem(Icons.Default.Person, "Account Settings")
        SettingsItem(Icons.Default.Settings, "App Settings")
        SettingsItem(Icons.Default.Notifications, "Notifications")
        SettingsItem(Icons.Default.Lock, "Privacy & Security")
        SettingsItem(Icons.Default.Star, "Premium Plan", true)
        SettingsItem(Icons.Default.Info, "Data Export")
        SettingsItem(Icons.AutoMirrored.Filled.ExitToApp, "Log Out", onClick = onLogout, isDestructive = true)
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { /* Delete Account Flow */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Delete Account", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, isPremium: Boolean = false, isDestructive: Boolean = false, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
        if (isPremium) {
            Box(modifier = Modifier.background(Color(0xFFFFD700).copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text("UPGRADE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB8860B))
            }
        }
    }
}
