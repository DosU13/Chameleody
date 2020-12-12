package com.example.chameleody

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.chameleody.activity.MainActivity.Companion.currentSongs
import com.example.chameleody.activity.MainActivity.Companion.currentSongPos
import com.example.chameleody.activity.MainActivity.Companion.currentShuffle
import com.example.chameleody.activity.MainActivity.Companion.REPEAT_ONE
import com.example.chameleody.activity.MainActivity.Companion.REPEAT_ALL
import com.example.chameleody.activity.MainActivity.Companion.SHUFFLE_ALL
import com.example.chameleody.activity.MainActivity.Companion.SHUFFLE_SMART

class MusicService : Service(), MediaPlayer.OnCompletionListener {
    private var mBinder: IBinder = MyBinder()
    lateinit var mediaPlayer: MediaPlayer
    val duration get() = mediaPlayer.duration
    val currentPosition get() = mediaPlayer.currentPosition
    val isPlaying get() = mediaPlayer.isPlaying
    private lateinit var uri : Uri

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder {
        Log.e("Bind", "Method")
        return mBinder
    }

    inner class MyBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val myPosition = intent.getIntExtra("servicePosition", -1)
        val actionName = intent.getStringExtra("ActionName")
        if (myPosition != -1) {
            playMedia(myPosition)
        }
        if (actionName!=null){
            when(actionName){
                "playPause" -> {
                    Toast.makeText(this, "PlayPause", Toast.LENGTH_LONG).show()
                    playPauseBtnClicked()
                }
                "next" -> {
                    Toast.makeText(this,"Next", Toast.LENGTH_SHORT).show()
                    nextBtnClicked()
                }
                "previous" -> {
                    Toast.makeText(this,"Previous", Toast.LENGTH_SHORT).show()
                    prevBtnClicked()
                }
            }
        }
        return START_STICKY
    }
    fun playMedia(startPosition : Int) {
        currentSongPos = startPosition
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        createMediaPlayer(currentSongPos)
        mediaPlayer.start()
    }

    fun createMediaPlayer(positionInner : Int) {
        if (positionInner < currentSongs.size) {
            currentSongPos = positionInner
            uri = Uri.parse(currentSongs[currentSongPos].path)
            mediaPlayer = MediaPlayer.create(baseContext, uri)
        }
    }

    fun playPauseBtnClicked(){

    }

    fun nextBtnClicked(){

    }

    fun prevBtnClicked(){

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

    fun onCompleted() {
        mediaPlayer.setOnCompletionListener(this)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        nextBtnClicked()
        createMediaPlayer(currentSongPos)
        start()
        onCompleted()
    }
}