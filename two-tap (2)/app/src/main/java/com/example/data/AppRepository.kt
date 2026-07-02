package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val savedContentDao: SavedContentDao,
    private val aiRecommendationDao: AiRecommendationDao
) {
    val allContent: Flow<List<SavedContent>> = savedContentDao.getAllContent()

    fun getContentByTime(time: Int): Flow<List<SavedContent>> = savedContentDao.getContentByTime(time)

    suspend fun insertContent(content: SavedContent): Long = savedContentDao.insertContent(content)

    suspend fun deleteContentById(id: Int) = savedContentDao.deleteContentById(id)

    // Recommendation Engine methods
    val topRecommendations: Flow<List<Recommendation>> = aiRecommendationDao.getTopRecommendations()
    val latestAiMemory: Flow<AiMemory?> = aiRecommendationDao.getLatestAiMemory()

    suspend fun insertAiAnalysis(analysis: AiAnalysis) = aiRecommendationDao.insertAiAnalysis(analysis)
    suspend fun insertRecommendation(recommendation: Recommendation) = aiRecommendationDao.insertRecommendation(recommendation)
    suspend fun insertLearningHistory(history: LearningHistory) = aiRecommendationDao.insertLearningHistory(history)
    suspend fun insertUserBehavior(behavior: UserBehavior) = aiRecommendationDao.insertUserBehavior(behavior)
    suspend fun insertAiMemory(memory: AiMemory) = aiRecommendationDao.insertAiMemory(memory)
    suspend fun insertAiChatHistory(chat: AiChatHistory) = aiRecommendationDao.insertAiChatHistory(chat)
    suspend fun insertUserFeedback(feedback: UserFeedback) = aiRecommendationDao.insertUserFeedback(feedback)
    suspend fun insertRecommendationMetrics(metrics: RecommendationMetrics) = aiRecommendationDao.insertRecommendationMetrics(metrics)
}
