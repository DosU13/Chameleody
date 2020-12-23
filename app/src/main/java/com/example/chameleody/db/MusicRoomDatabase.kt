package com.example.chameleody.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.chameleody.model.MusicFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [MusicFile::class], version = 1, exportSchema = false)
abstract class MusicRoomDatabase : RoomDatabase() {
    abstract fun musicFilesDao(): MusicFilesDao

    private class MusicDatabaseCallBack(private val scope: CoroutineScope) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database -> scope.launch {
                populateDatabase(database.musicFilesDao())
            } }
        }

        suspend fun populateDatabase(musicFilesDao: MusicFilesDao){
            musicFilesDao.deleteAll()

            var musicFile = MusicFile("path", "title", "artist", "album", "duration", "sp_id", "dateAdded")
            musicFilesDao.insert(musicFile)
            musicFile = MusicFile("empty", "empty", "empty", "empty", "empty", "empty", "empty")
            musicFilesDao.insert(musicFile)
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: MusicRoomDatabase? = null
        fun getDatabase(context: Context, scope: CoroutineScope): MusicRoomDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicRoomDatabase::class.java,
                    "music_files_database"
                ).addCallback(MusicDatabaseCallBack(scope)).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
