package com.example.chameleody.db

import androidx.room.*
import com.example.chameleody.model.MusicFile
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicFilesDao {
    @Query("SELECT * FROM MusicFiles")
    fun getAll(): Flow<List<MusicFile>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(MusicFile: MusicFile)

    @Delete
    suspend fun delete(MusicFile: MusicFile)

    @Update
    suspend fun update(MusicFile: MusicFile)

    @Query("DELETE FROM MusicFiles")
    suspend fun deleteAll()
}