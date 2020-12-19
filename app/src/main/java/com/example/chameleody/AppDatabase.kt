package com.example.chameleody

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chameleody.model.MyMusic

@Database(entities = [MyMusic::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun myMusicDao(): MyMusicDao
}