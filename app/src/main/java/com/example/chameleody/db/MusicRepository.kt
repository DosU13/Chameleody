package com.example.chameleody.db

import androidx.annotation.WorkerThread
import com.example.chameleody.db.MusicFilesDao
import com.example.chameleody.model.MusicFile
import kotlinx.coroutines.flow.Flow

class MusicRepository(private val musicFilesDao: MusicFilesDao) {
    val allMusics: Flow<List<MusicFile>> = musicFilesDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(musicFile: MusicFile){
        musicFilesDao.insert(musicFile)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(musicFile: MusicFile){
        musicFilesDao.update(musicFile)
    }
}