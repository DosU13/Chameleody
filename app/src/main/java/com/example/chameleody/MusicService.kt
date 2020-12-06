package com.example.dreamplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.dreamplayer.activity.PlayerActivity.Companion.listSongs
import com.example.dreamplayer.model.MusicFiles

class MusicService : Service(), MediaPlayer.OnCompletionListener {
    var mBinder: IBinder = MyBinder()
    lateinit var mediaPlayer: MediaPlayer
    val duration get() = mediaPlayer.duration
    val currentPosition get() = mediaPlayer.currentPosition
    val isPlaying get() = mediaPlayer.isPlaying
    private var musicFiles = ArrayList<MusicFiles>()
    private lateinit var uri : Uri
    var position: Int = -1
    lateinit var actionPlaying: ActionPlaying

    override fun onCreate() {
        super.onCreate()
        musicFiles = listSongs
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e("Bind", "Method")
        return mBinder
    }

    inner class MyBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val myPosition = intent.getIntExtra("servicePosition", -1)
        if (myPosition != -1) {
            playMedia(myPosition)
        }
        return START_STICKY
    }
    fun playMedia(startPosiotion : Int) {
        musicFiles = listSongs
        position = startPosiotion
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        createMediaPlayer(position)
        mediaPlayer.start()
    }


    fun start(){
        mediaPlayer.start()
    }

    fun isPLaying() : Boolean{
        return mediaPlayer.isPlaying
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun reset() {
        mediaPlayer.reset()
    }

    fun stop() {
        mediaPlayer.stop()
    }

    fun release() {
        mediaPlayer.release()
    }

    fun seekTo(position : Int){
        mediaPlayer.seekTo(position)
    }

    fun createMediaPlayer(position : Int) {
        uri = Uri.parse(musicFiles[position].path)
        mediaPlayer = MediaPlayer.create(baseContext, uri)
    }

    fun onCompleted() {
        mediaPlayer.setOnCompletionListener(this)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        if (::actionPlaying.isInitialized) {
            actionPlaying.nextBtnClicked()
        }
        createMediaPlayer(position)
        start()
        onCompleted()
    }
}