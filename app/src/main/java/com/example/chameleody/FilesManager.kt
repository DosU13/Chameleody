package com.example.chameleody

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.chameleody.activity.MainActivity
import com.example.chameleody.db.MusicViewModel
import com.example.chameleody.model.MusicFile

class FilesManager {
    companion object {
        val instance = FilesManager()
    }

    var currentSongs = ArrayList<MusicFile>()
    var currentSongPos = 0
    var spMusicFiles = ArrayList<MusicFile>()
    var dbMusicFiles = ArrayList<MusicFile>()
    var currentShuffle = 2
    var albums = ArrayList<MusicFile>()
    val currentSong: MusicFile get() = currentSongs[currentSongPos]
    lateinit var mainActivity: MainActivity
    lateinit var musicViewModel: MusicViewModel

    fun viewModelReady(musics: List<MusicFile>){
        Toast.makeText(mainActivity, "db is ready, man", Toast.LENGTH_LONG).show()
        dbMusicFiles = musics as ArrayList<MusicFile>
        mergeAll()
    }

    fun getAllAudio(mainActivity: MainActivity) {
        this.mainActivity = mainActivity
        getAllFromSP()
    }

    private fun getAllFromSP() {
        val preferences =
            mainActivity.getSharedPreferences(MainActivity.MY_SORT_PREF, Context.MODE_PRIVATE)
        val sortOrder = preferences.getString("sorting", "sortByDate")
        val duplicate = ArrayList<String>()
        albums.clear()
        val tempAudioList = ArrayList<MusicFile>()
        var order = MediaStore.MediaColumns.DATE_ADDED + " ASC"
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        when (sortOrder) {
            "sortByName" -> order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC"
            "sortByDate" -> order = MediaStore.MediaColumns.DATE_ADDED + " ASC"
            "sortBySize" -> order = MediaStore.MediaColumns.SIZE + " DESC"
        }
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATE_ADDED
        )
        val cursor = mainActivity.contentResolver.query(uri, projection, null, null, order)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val album = cursor.getString(0)
                val title = cursor.getString(1)
                val duration = cursor.getString(2)
                val path = cursor.getString(3)
                val artist = cursor.getString(4)
                val spId = cursor.getString(5)
                val dateAdded = cursor.getString(6)

                val musicFiles = MusicFile(path, title, artist, album, duration, spId, dateAdded)
                tempAudioList.add(musicFiles)
                if (!duplicate.contains(album)) {
                    albums.add(musicFiles)
                    duplicate.add(album)
                }
            }
            cursor.close()
        }
        spMusicFiles = tempAudioList
    }

    private fun mergeAll() {
        for (it in 0 until spMusicFiles.size) {
            var isNew = true
            for (dbMus in dbMusicFiles) {
                if (spMusicFiles[it].sp_id == dbMus.sp_id) {
                    spMusicFiles[it] = dbMus
                    isNew = false
                }
            }
            if (isNew) {
                saveToDB(spMusicFiles[it])
            }
        }
    }

    private fun saveToDB(music: MusicFile){
        musicViewModel.insert(music)
        Log.e("LOOK HERE", music.name)
    }
}
