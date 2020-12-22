package com.example.chameleody

import androidx.room.*
import com.example.chameleody.model.MusicFiles

@Dao
interface MusicFilesDao {
    @Query("SELECT * FROM MusicFiles")
    fun getAll(): List<MusicFiles>

    @Insert
    fun insert(MusicFiles: MusicFiles)

    @Delete
    fun delete(MusicFiles: MusicFiles)

    @Update
    fun update(MusicFiles: MusicFiles)
}