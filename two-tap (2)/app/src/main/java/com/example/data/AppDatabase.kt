package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SavedContent::class,
        AiAnalysis::class,
        Recommendation::class,
        LearningHistory::class,
        UserBehavior::class,
        AiMemory::class,
        AiChatHistory::class,
        UserFeedback::class,
        RecommendationMetrics::class
    ], 
    version = 3, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedContentDao(): SavedContentDao
    abstract fun aiRecommendationDao(): AiRecommendationDao
}
