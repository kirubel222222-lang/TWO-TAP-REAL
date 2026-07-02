package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedContentDao {
    @Query("SELECT * FROM saved_content ORDER BY timestamp DESC")
    fun getAllContent(): Flow<List<SavedContent>>

    @Query("SELECT * FROM saved_content WHERE estimatedTime <= :time ORDER BY timestamp DESC")
    fun getContentByTime(time: Int): Flow<List<SavedContent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: SavedContent): Long

    @Query("DELETE FROM saved_content WHERE id = :id")
    suspend fun deleteContentById(id: Int)
}
