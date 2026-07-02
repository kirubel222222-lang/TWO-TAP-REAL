package com.example.domain

import com.example.data.*
import kotlinx.coroutines.flow.firstOrNull

class AiRecommendationEngine(private val repository: AppRepository) {

    suspend fun generateRecommendations(
        mode: String, 
        timeMinutes: Int, 
        userId: String = ""
    ): List<Recommendation> {
        val allContent = repository.allContent.firstOrNull() ?: emptyList()
        val userBehavior = repository.latestAiMemory.firstOrNull()
        
        // Filter content based on mode (Read, Watch, Listen)
        val filteredByMode = allContent.filter { content ->
            when (mode) {
                "Watch" -> content.type == "Video" || content.contentType == "Video"
                "Listen" -> content.type == "Podcast" || content.contentType == "Podcast"
                "Read" -> content.type == "Article" || content.contentType == "Article"
                else -> true
            }
        }
        
        // Filter out completed content
        val pendingContent = filteredByMode.filter { it.status != "Completed" }
        
        // Scoring logic
        val scoredItems = pendingContent.map { content ->
            val timeScore = calculateTimeMatchScore(content.estimatedTime, timeMinutes)
            val interestScore = calculateInterestMatchScore(content, userBehavior)
            
            val totalScore = (timeScore * 0.4f) + (interestScore * 0.6f)
            
            Pair(content, totalScore)
        }
        
        val topPicks = scoredItems.sortedByDescending { it.second }.take(5)
        
        val recommendations = topPicks.map { (content, score) ->
            Recommendation(
                userId = userId,
                contentId = content.id,
                recommendationScore = score,
                recommendationReason = "Based on your interest in ${content.category} and available time.",
                recommendationType = if (timeMinutes <= 10) "Quick Learn" else "Deep Learning"
            )
        }
        
        recommendations.forEach {
            repository.insertRecommendation(it)
        }
        
        return recommendations
    }
    
    private fun calculateTimeMatchScore(contentTime: Int, availableTime: Int): Float {
        if (availableTime == 0) return 1.0f // Surprise me
        
        return when {
            contentTime <= availableTime -> 1.0f
            contentTime <= availableTime + 5 -> 0.8f
            else -> 0.3f
        }
    }
    
    private fun calculateInterestMatchScore(content: SavedContent, aiMemory: AiMemory?): Float {
        if (aiMemory == null) return 0.5f // Neutral score if no memory
        
        var score = 0.5f
        if (aiMemory.strongestInterests.contains(content.category, ignoreCase = true)) {
            score += 0.3f
        }
        if (aiMemory.favoriteContentType.equals(content.contentType, ignoreCase = true)) {
            score += 0.2f
        }
        
        return score.coerceIn(0f, 1.0f)
    }
}
