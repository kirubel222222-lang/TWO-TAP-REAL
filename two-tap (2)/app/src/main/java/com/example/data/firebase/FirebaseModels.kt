package com.example.data.firebase

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

// ------------------------------------------------------------------------
// FIREBASE FIRESTORE ARCHITECTURE
// These models match the Firebase NoSQL collections structure.
// ------------------------------------------------------------------------

data class FirebaseUser(
    @DocumentId val id: String = "",
    @PropertyName("full_name") val fullName: String = "",
    val username: String = "",
    val email: String = "",
    @PropertyName("profile_image") val profileImage: String? = null,
    val bio: String? = null,
    @PropertyName("plan_type") val planType: String = "Free",
    @PropertyName("onboarding_completed") val onboardingCompleted: Boolean = false,
    @PropertyName("email_verified") val emailVerified: Boolean = false,
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis(),
    @PropertyName("updated_at") val updatedAt: Long = System.currentTimeMillis()
)

data class UserPreference(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    val theme: String = "System",
    val language: String = "en",
    @PropertyName("notification_enabled") val notificationEnabled: Boolean = true,
    @PropertyName("preferred_learning_time") val preferredLearningTime: String? = null,
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis(),
    @PropertyName("updated_at") val updatedAt: Long = System.currentTimeMillis()
)

data class UserInterest(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    @PropertyName("interest_name") val interestName: String = "",
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis()
)

data class LearningGoal(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    @PropertyName("goal_name") val goalName: String = "",
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis()
)

data class FirebaseSavedContent(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    val title: String = "",
    val url: String = "",
    @PropertyName("source_platform") val sourcePlatform: String = "",
    @PropertyName("content_type") val contentType: String = "",
    @PropertyName("thumbnail_url") val thumbnailUrl: String? = null,
    val category: String = "",
    val tags: List<String> = emptyList(),
    @PropertyName("estimated_time") val estimatedTime: Int = 0,
    @PropertyName("difficulty_level") val difficultyLevel: String = "Beginner",
    val status: String = "Saved",
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis(),
    @PropertyName("updated_at") val updatedAt: Long = System.currentTimeMillis()
)

data class ContentAnalysis(
    @DocumentId val id: String = "",
    @PropertyName("content_id") val contentId: String = "",
    @PropertyName("quick_summary") val quickSummary: String = "",
    @PropertyName("detailed_summary") val detailedSummary: String = "",
    @PropertyName("key_takeaways") val keyTakeaways: List<String> = emptyList(),
    @PropertyName("action_steps") val actionSteps: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
    val sentiment: String = "",
    @PropertyName("difficulty_level") val difficultyLevel: String = "",
    @PropertyName("generated_at") val generatedAt: Long = System.currentTimeMillis()
)

data class UserNote(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    @PropertyName("content_id") val contentId: String = "",
    @PropertyName("note_text") val noteText: String = "",
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis(),
    @PropertyName("updated_at") val updatedAt: Long = System.currentTimeMillis()
)

data class ContentHighlight(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    @PropertyName("content_id") val contentId: String = "",
    @PropertyName("highlight_text") val highlightText: String = "",
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis()
)

data class Category(
    @DocumentId val id: String = "",
    @PropertyName("category_name") val categoryName: String = "",
    val icon: String? = null,
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis()
)

data class ContentTag(
    @DocumentId val id: String = "",
    @PropertyName("content_id") val contentId: String = "",
    @PropertyName("tag_name") val tagName: String = ""
)

data class LearningSession(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    @PropertyName("content_id") val contentId: String = "",
    @PropertyName("session_duration") val sessionDuration: Int = 0,
    val completed: Boolean = false,
    @PropertyName("started_at") val startedAt: Long = System.currentTimeMillis(),
    @PropertyName("completed_at") val completedAt: Long? = null
)

data class Streak(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    @PropertyName("current_streak") val currentStreak: Int = 0,
    @PropertyName("longest_streak") val longestStreak: Int = 0,
    @PropertyName("last_activity_date") val lastActivityDate: Long = System.currentTimeMillis()
)

data class Achievement(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    @PropertyName("achievement_name") val achievementName: String = "",
    @PropertyName("achievement_description") val achievementDescription: String = "",
    val icon: String = "",
    @PropertyName("earned_at") val earnedAt: Long = System.currentTimeMillis()
)

data class Recommendation(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    @PropertyName("content_id") val contentId: String = "",
    @PropertyName("recommendation_score") val recommendationScore: Float = 0f,
    @PropertyName("recommendation_reason") val recommendationReason: String = "",
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis()
)

data class Notification(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    val title: String = "",
    val message: String = "",
    @PropertyName("notification_type") val notificationType: String = "",
    @PropertyName("is_read") val isRead: Boolean = false,
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis()
)

data class AiConversation(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    @PropertyName("content_id") val contentId: String? = null,
    @PropertyName("user_message") val userMessage: String = "",
    @PropertyName("ai_response") val aiResponse: String = "",
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis()
)

data class Subscription(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    @PropertyName("subscription_type") val subscriptionType: String = "",
    val status: String = "",
    @PropertyName("start_date") val startDate: Long = System.currentTimeMillis(),
    @PropertyName("end_date") val endDate: Long? = null,
    @PropertyName("payment_provider") val paymentProvider: String? = null
)

data class Analytics(
    @DocumentId val id: String = "",
    @PropertyName("user_id") val userId: String = "",
    @PropertyName("event_name") val eventName: String = "",
    @PropertyName("event_data") val eventData: String = "", // JSON string
    @PropertyName("device_type") val deviceType: String = "",
    val platform: String = "",
    @PropertyName("created_at") val createdAt: Long = System.currentTimeMillis()
)
