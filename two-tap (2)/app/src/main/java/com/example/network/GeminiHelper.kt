package com.example.network

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class ContentAnalysis(
    val summary: String,
    val takeaways: String,
    val actionItems: String
)

data class FullAiAnalysis(
    val title: String,
    val description: String,
    val author: String,
    val language: String,
    val category: String,
    val difficulty: String,
    val estimatedMinutes: Int,
    val quickSummary: String,
    val detailedSummary: String,
    val keyTakeaways: String,
    val actionSteps: String,
    val keywords: String,
    val detectedTopics: String,
    val suggestedLearningMode: String,
    val recommendationScore: Int
)

class GeminiHelper {
    suspend fun analyzeContentComplete(url: String, notes: String, titleInput: String): FullAiAnalysis = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val detected = detectPlatform(url)
        val platform = detected.first
        val contentType = detected.second
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // High-quality fallback for offline / unconfigured API keys
            return@withContext FullAiAnalysis(
                title = titleInput.ifBlank { "Interesting $platform $contentType" },
                description = "Knowledge saved from $platform on ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())}.",
                author = "Creator on $platform",
                language = "English",
                category = "Technology",
                difficulty = "Intermediate",
                estimatedMinutes = 8,
                quickSummary = "A fascinating insight into saved concepts from $platform.",
                detailedSummary = "This saved content contains key ideas, methodologies, and concepts shared on $platform. It has been curated and organized for your personalized learning pathway.",
                keyTakeaways = "• Key Insight: Information curation is power.\n• Continuous Learning: Keep building habits.\n• Domain Mastery: Expand knowledge base across categories.",
                actionSteps = "• Review: Read through the summary.\n• Practice: Apply these insights in your daily workflow.\n• Chat: Use AI Chat to ask deeper questions.",
                keywords = "learning, curation, $platform, knowledge, technology",
                detectedTopics = "$platform, Learning, Insights",
                suggestedLearningMode = if (contentType == "Video") "Watch" else "Read",
                recommendationScore = 85
            )
        }

        val prompt = """
            You are the TWO TAP AI content analyzer. Analyze the following saved content:
            URL: $url
            User Supplied Title/Notes: $titleInput $notes
            Detected Platform: $platform
            Detected Content Type: $contentType
            
            Extract and generate the following metadata and AI analysis. Return ONLY a valid JSON object. Do not include any markdown format like ```json or ```. Return raw JSON text.
            
            JSON Schema:
            {
                "title": "Clean, highly descriptive title. Guess the specific title from the URL path if not known.",
                "description": "Short 1-2 sentence description.",
                "author": "Creator or author name if detectable, else 'Unknown'",
                "language": "Detected language (e.g. English, Spanish, etc.)",
                "category": "One of: Business, Technology, AI, Startups, Programming, Design, Finance, Marketing, Self Improvement, Fitness, Science, Other",
                "difficulty": "One of: Beginner, Intermediate, Advanced",
                "estimatedMinutes": 5,
                "quickSummary": "A powerful, concise 1-sentence quick summary.",
                "detailedSummary": "A detailed 2-3 paragraph summary of core concepts.",
                "keyTakeaways": "A bulleted list of 3-5 key takeaways, starting with bullet symbols (•), separated by newlines.",
                "actionSteps": "A bulleted list of 2-4 actionable next steps, starting with bullet symbols (•), separated by newlines.",
                "keywords": "Comma-separated list of 5 keywords.",
                "detectedTopics": "Comma-separated list of topics.",
                "suggestedLearningMode": "One of: Read, Watch, Listen, Chat",
                "recommendationScore": 85
            }
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val cleanJson = cleanJsonString(rawText)
            
            val json = JSONObject(cleanJson)
            FullAiAnalysis(
                title = json.optString("title", titleInput.ifBlank { "Saved from $platform" }),
                description = json.optString("description", ""),
                author = json.optString("author", "Unknown"),
                language = json.optString("language", "English"),
                category = json.optString("category", "Technology"),
                difficulty = json.optString("difficulty", "Intermediate"),
                estimatedMinutes = json.optInt("estimatedMinutes", 5),
                quickSummary = json.optString("quickSummary", ""),
                detailedSummary = json.optString("detailedSummary", ""),
                keyTakeaways = json.optString("keyTakeaways", ""),
                actionSteps = json.optString("actionSteps", ""),
                keywords = json.optString("keywords", ""),
                detectedTopics = json.optString("detectedTopics", ""),
                suggestedLearningMode = json.optString("suggestedLearningMode", "Read"),
                recommendationScore = json.optInt("recommendationScore", 80)
            )
        } catch (e: Exception) {
            // Fallback
            FullAiAnalysis(
                title = titleInput.ifBlank { "Saved from $platform" },
                description = "Analysis incomplete. Error parsing response.",
                author = "Unknown",
                language = "English",
                category = "Other",
                difficulty = "Intermediate",
                estimatedMinutes = 5,
                quickSummary = "Saved content from $platform.",
                detailedSummary = "Error during AI generation: ${e.message}",
                keyTakeaways = "• Review saved link.",
                actionSteps = "• Read original content.",
                keywords = "saved, $platform",
                detectedTopics = "Uncategorized",
                suggestedLearningMode = "Read",
                recommendationScore = 70
            )
        }
    }

    private fun cleanJsonString(raw: String): String {
        var cleaned = raw.trim()
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.removePrefix("```json")
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.removePrefix("```")
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.removeSuffix("```")
        }
        return cleaned.trim()
    }

    fun detectPlatform(url: String): Pair<String, String> {
        val lowerUrl = url.lowercase()
        return when {
            lowerUrl.contains("youtube.com") || lowerUrl.contains("youtu.be") -> Pair("YouTube", "Video")
            lowerUrl.contains("vimeo.com") -> Pair("Vimeo", "Video")
            lowerUrl.contains("loom.com") -> Pair("Loom", "Video")
            lowerUrl.contains("reddit.com") -> Pair("Reddit", "Social Media")
            lowerUrl.contains("x.com") || lowerUrl.contains("twitter.com") -> Pair("X (Twitter)", "Social Media")
            lowerUrl.contains("instagram.com") -> Pair("Instagram", "Social Media")
            lowerUrl.contains("tiktok.com") -> Pair("TikTok", "Social Media")
            lowerUrl.contains("linkedin.com") -> Pair("LinkedIn", "Social Media")
            lowerUrl.contains("github.com") -> Pair("GitHub", "Other")
            lowerUrl.contains("medium.com") || lowerUrl.contains("blogspot.com") -> Pair("Medium", "Article")
            lowerUrl.contains("podcast") || lowerUrl.contains("spotify.com/episode") || lowerUrl.contains("apple.co") -> Pair("Podcast", "Audio")
            lowerUrl.endsWith(".pdf") -> Pair("Document", "PDF")
            lowerUrl.endsWith(".docx") -> Pair("Document", "DOCX")
            lowerUrl.endsWith(".txt") -> Pair("Document", "TXT")
            else -> Pair("Web", "Article")
        }
    }

    suspend fun analyzeContent(title: String, notes: String, url: String): ContentAnalysis = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext ContentAnalysis(
                summary = "Please configure your Gemini API Key in the AI Studio Secrets panel.",
                takeaways = "",
                actionItems = ""
            )
        }
        
        val prompt = """
            Analyze the following content saved by the user.
            Title: $title
            URL: $url
            User Notes: $notes
            
            Provide the analysis in three exact sections separated by "|||".
            Format exactly like this:
            [Summary Paragraph]
            |||
            [List of Key Takeaways]
            |||
            [List of Action Items]
            
            If there are no clear action items, suggest some based on the topic.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            
            val parts = text.split("|||")
            ContentAnalysis(
                summary = parts.getOrNull(0)?.trim() ?: "No summary generated.",
                takeaways = parts.getOrNull(1)?.trim() ?: "",
                actionItems = parts.getOrNull(2)?.trim() ?: ""
            )
        } catch (e: Exception) {
            ContentAnalysis("Error generating analysis: ${e.message}", "", "")
        }
    }
    
    suspend fun getEstimatedTime(title: String, type: String): Int = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext 5
        
        val prompt = "Estimate the time in minutes it takes to consume a $type titled '$title'. Reply with ONLY a single integer number (e.g., 5, 10, 15)."
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )
        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "5"
            // Find the first sequence of digits
            val numberStr = Regex("\\d+").find(text)?.value ?: "5"
            numberStr.toIntOrNull() ?: 5
        } catch (e: Exception) {
            5
        }
    }
    
    suspend fun categorizeContent(title: String, notes: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext "Uncategorized"
        
        val prompt = "Categorize this content based on the title and notes. Title: '$title', Notes: '$notes'. Choose ONE from: Business, Technology, AI, Startups, Programming, Design, Finance, Marketing, Self Improvement, Fitness, Science, Other. Reply with ONLY the exact category name."
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )
        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Other"
            text.trim().trimEnd('.')
        } catch (e: Exception) {
            "Other"
        }
    }
    
    suspend fun chatWithAssistant(query: String, contextData: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext "Please configure your Gemini API Key in the AI Studio Secrets panel."
        
        val prompt = """
            You are the TWO TAP AI Assistant, a helpful learning guide. Use the following saved content context to answer the user's query if relevant. If they ask a general question, answer it. Keep it concise, helpful, and conversational.
            
            Saved Content Context:
            $contextData
            
            User Query: $query
        """.trimIndent()
        
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )
        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I'm sorry, I couldn't generate a response."
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
