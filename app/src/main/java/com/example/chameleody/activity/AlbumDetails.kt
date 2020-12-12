package com.example.chameleody.activity

import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chameleody.R
import com.example.chameleody.activity.MainActivity.Companion.musicFiles
import com.example.chameleody.adapter.AlbumDetailsAdapter
import com.example.chameleody.model.MusicFiles

class AlbumDetails : AppCompatActivity() {
    private val albumSongs = ArrayList<MusicFiles>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_details)
        val albumPhoto = findViewById<ImageView>(R.id.album_album_photo)
        val albumName = intent.getStringExtra("albumName")
        var j = 0
        for (i in 0 until musicFiles.size){
            if (albumName.equals(musicFiles[i].album)){
                albumSongs.add(j, musicFiles[i])
                j++
            }
        }
        val image = getAlbumArt(albumSongs[0].path)
        if (image!=null){
            Glide.with(this).load(image).into(albumPhoto)
        }
        else{
            Glide.with(this).load(R.drawable.default_art).into(albumPhoto)
        }
    }

    override fun onResume() {
        super.onResume()
        if (albumSongs.size >= 1){
            val albumDetailsAdapter = AlbumDetailsAdapter(this, albumSongs)
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.adapter = albumDetailsAdapter
            recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}