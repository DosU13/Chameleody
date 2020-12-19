package com.example.chameleody

import android.content.Context
import androidx.room.Room

class DatabaseClient(mCtx: Context) {
    private val appDatabase: AppDatabase = Room.databaseBuilder(mCtx, AppDatabase::class.java, "MyMusics").build()

    companion object{
        lateinit var mInstance: DatabaseClient
        @Synchronized
        public fun getInstance(mCtx: Context): DatabaseClient{
            if(this::mInstance.isInitialized) mInstance = DatabaseClient(mCtx)
            return mInstance
        }
    }

    fun getAppDatabase() : AppDatabase {return appDatabase}
}