package com.example.chameleody

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chameleody.model.MusicFiles

@Database(entities = [MusicFiles::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicFilesDao(): MusicFilesDao
}