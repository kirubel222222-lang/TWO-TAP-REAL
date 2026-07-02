package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_analysis")
data class AiAnalysis(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contentId: Int,
    val quickSummary: String = "",
    val detailedSummary: String = "",
    val keyTakeaways: String = "",
    val actionSteps: String = "",
    val keywords: String = "",
    val learningTopics: String = "",
    val estimatedTime: Int = 0,
    val difficultyScore: Int = 0,
    val qualityScore: Int = 0,
    val sentiment: String = "",
    val generatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recommendations")
data class Recommendation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",
    val contentId: Int,
    val recommendationScore: Float = 0f,
    val recommendationReason: String = "",
    val recommendationType: String = "",
    val wasOpened: Boolean = false,
    val wasCompleted: Boolean = false,
    val wasSkipped: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "learning_history")
data class LearningHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",
    val contentId: Int,
    val startedAt: Long = System.currentTimeMillis(),
    val finishedAt: Long = 0,
    val completionPercentage: Int = 0,
    val learningDuration: Int = 0, // in minutes
    val completed: Boolean = false,
    val skipped: Boolean = false
)

@Entity(tableName = "user_behavior")
data class UserBehavior(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",
    val preferredLearningMode: String = "",
    val preferredLearningTime: Int = 0,
    val averageSessionLength: Int = 0,
    val favoritePlatform: String = "",
    val favoriteCategory: String = "",
    val mostActiveTime: String = "",
    val lastLearningDate: Long = 0
)

@Entity(tableName = "ai_memory")
data class AiMemory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",
    val strongestInterests: String = "",
    val weakestTopics: String = "",
    val preferredDifficulty: String = "",
    val favoriteContentType: String = "",
    val learningStyle: String = "",
    val recommendationVersion: Int = 1,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_chat_history")
data class AiChatHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",
    val contentId: Int = 0,
    val userPrompt: String = "",
    val aiResponse: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_feedback")
data class UserFeedback(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",
    val contentId: Int,
    val rating: Int = 0,
    val helpful: Boolean = false,
    val feedbackText: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recommendation_metrics")
data class RecommendationMetrics(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recommendationId: Int,
    val clicked: Boolean = false,
    val opened: Boolean = false,
    val completed: Boolean = false,
    val ignored: Boolean = false,
    val timeSpent: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
