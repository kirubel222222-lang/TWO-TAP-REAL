package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiHelper
import com.example.domain.AiRecommendationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    private val aiRecommendationEngine = AiRecommendationEngine(repository)

    val allContent: StateFlow<List<SavedContent>> = repository.allContent
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    val topRecommendations: StateFlow<List<Recommendation>> = repository.topRecommendations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val geminiHelper = GeminiHelper()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving
    
    private val _currentRecommendations = MutableStateFlow<List<Recommendation>>(emptyList())
    val currentRecommendations: StateFlow<List<Recommendation>> = _currentRecommendations

    fun getCurrentUserId(): String {
        return try {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            auth.currentUser?.uid ?: "local_user"
        } catch (e: Exception) {
            "local_user"
        }
    }

    fun findDuplicateContent(url: String): SavedContent? {
        return allContent.value.find { it.url.trim().lowercase() == url.trim().lowercase() }
    }

    fun getYoutubeThumbnail(url: String): String {
        val videoId = when {
            url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            else -> null
        }
        return if (videoId != null) "https://img.youtube.com/vi/$videoId/hqdefault.jpg" else ""
    }

    suspend fun runAiAnalysis(url: String, titleInput: String, notesInput: String): com.example.network.FullAiAnalysis {
        return geminiHelper.analyzeContentComplete(url, notesInput, titleInput)
    }

    fun saveContentWithAnalysis(
        url: String,
        type: String,
        notes: String,
        analysis: com.example.network.FullAiAnalysis
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            
            val userId = getCurrentUserId()
            val detected = geminiHelper.detectPlatform(url)
            val platform = detected.first
            val thumbnailUrl = if (platform == "YouTube") {
                getYoutubeThumbnail(url)
            } else {
                "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?q=80&w=600"
            }
            
            val newContent = SavedContent(
                userId = userId,
                url = url,
                title = analysis.title,
                notes = notes,
                type = type,
                contentType = type,
                sourcePlatform = platform,
                thumbnailUrl = thumbnailUrl,
                author = analysis.author,
                estimatedReadingTime = analysis.estimatedMinutes,
                estimatedTime = analysis.estimatedMinutes,
                difficultyLevel = analysis.difficulty,
                language = analysis.language,
                category = analysis.category,
                tags = analysis.keywords,
                aiSummary = analysis.quickSummary,
                keyTakeaways = analysis.keyTakeaways,
                actionItems = analysis.actionSteps,
                aiStatus = "Completed",
                status = "Unread",
                timestamp = System.currentTimeMillis(),
                savedAt = System.currentTimeMillis()
            )
            
            val contentId = repository.insertContent(newContent).toInt()
            
            val aiAnalysisObj = AiAnalysis(
                contentId = contentId,
                quickSummary = analysis.quickSummary,
                detailedSummary = analysis.detailedSummary,
                keyTakeaways = analysis.keyTakeaways,
                actionSteps = analysis.actionSteps,
                keywords = analysis.keywords,
                learningTopics = analysis.detectedTopics,
                estimatedTime = analysis.estimatedMinutes,
                difficultyScore = when (analysis.difficulty) {
                    "Beginner" -> 30
                    "Intermediate" -> 60
                    "Advanced" -> 90
                    else -> 50
                },
                qualityScore = analysis.recommendationScore,
                sentiment = "Objective"
            )
            repository.insertAiAnalysis(aiAnalysisObj)
            
            try {
                val newMemory = AiMemory(
                    userId = userId,
                    strongestInterests = analysis.category,
                    favoriteContentType = type,
                    preferredDifficulty = analysis.difficulty,
                    updatedAt = System.currentTimeMillis()
                )
                repository.insertAiMemory(newMemory)
            } catch (e: Exception) {
                // Ignore memory insertion error
            }
            
            _isSaving.value = false
        }
    }

    fun saveContent(url: String, title: String, notes: String, type: String) {
        viewModelScope.launch {
            _isSaving.value = true
            
            val analysis = geminiHelper.analyzeContent(title, notes, url)
            val estimatedTime = geminiHelper.getEstimatedTime(title, type)
            val category = geminiHelper.categorizeContent(title, notes)
            val userId = getCurrentUserId()
            
            val newContent = SavedContent(
                userId = userId,
                url = url,
                title = title,
                notes = notes,
                type = type,
                contentType = type,
                category = category,
                aiSummary = analysis.summary,
                keyTakeaways = analysis.takeaways,
                actionItems = analysis.actionItems,
                estimatedTime = estimatedTime,
                status = "Unread"
            )
            repository.insertContent(newContent)
            
            _isSaving.value = false
        }
    }

    fun deleteContent(id: Int) {
        viewModelScope.launch {
            repository.deleteContentById(id)
        }
    }
    
    suspend fun sendChatQuery(query: String): String {
        val currentContent = allContent.value
        val contextData = currentContent.joinToString("\n") { 
            "Title: ${it.title}, Type: ${it.type}, Category: ${it.category}, Notes: ${it.notes}, Summary: ${it.aiSummary}, Key Takeaways: ${it.keyTakeaways}" 
        }
        return geminiHelper.chatWithAssistant(query, contextData)
    }

    fun generateRecommendations(mode: String, timeMinutes: Int) {
        viewModelScope.launch {
            val recs = aiRecommendationEngine.generateRecommendations(mode, timeMinutes)
            _currentRecommendations.value = recs
        }
    }
    
    fun saveAiAnalysis(analysis: AiAnalysis) {
        viewModelScope.launch {
            repository.insertAiAnalysis(analysis)
        }
    }

    fun logLearningHistory(history: LearningHistory) {
        viewModelScope.launch {
            repository.insertLearningHistory(history)
        }
    }

    fun updateUserBehavior(behavior: UserBehavior) {
        viewModelScope.launch {
            repository.insertUserBehavior(behavior)
        }
    }
}
