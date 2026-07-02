package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_content")
data class SavedContent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",
    val url: String,
    val title: String,
    val sourcePlatform: String = "",
    val contentType: String = "Article", // type rename
    val type: String = "Article", // keep original for back-compat or refactoring
    val thumbnailUrl: String = "",
    val author: String = "",
    val publishedDate: Long = 0,
    val durationMinutes: Int = 0,
    val estimatedReadingTime: Int = 0,
    val difficultyLevel: String = "",
    val language: String = "",
    val category: String = "Uncategorized",
    val tags: String = "",
    val aiStatus: String = "",
    val savedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    
    // original fields
    val notes: String = "",
    val aiSummary: String = "",
    val keyTakeaways: String = "",
    val actionItems: String = "",
    val estimatedTime: Int = 5, // minutes
    val status: String = "Unread", // Unread, Completed
    val timestamp: Long = System.currentTimeMillis()
)
