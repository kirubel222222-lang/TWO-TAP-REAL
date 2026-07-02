package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AiRecommendationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiAnalysis(analysis: AiAnalysis)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendation(recommendation: Recommendation)

    @Query("SELECT * FROM recommendations ORDER BY recommendationScore DESC LIMIT 5")
    fun getTopRecommendations(): Flow<List<Recommendation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningHistory(history: LearningHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserBehavior(behavior: UserBehavior)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiMemory(memory: AiMemory)

    @Query("SELECT * FROM ai_memory ORDER BY updatedAt DESC LIMIT 1")
    fun getLatestAiMemory(): Flow<AiMemory?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiChatHistory(chat: AiChatHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserFeedback(feedback: UserFeedback)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendationMetrics(metrics: RecommendationMetrics)
}
