package com.example.chameleody

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.chameleody.activity.MainActivity.Companion.REPEAT_ALL
import com.example.chameleody.activity.MainActivity.Companion.SHUFFLE_ALL
import com.example.chameleody.activity.MainActivity.Companion.SHUFFLE_SMART
import com.example.chameleody.activity.MainActivity.Companion.currentShuffle
import com.example.chameleody.activity.MainActivity.Companion.currentSongPos
import com.example.chameleody.activity.MainActivity.Companion.currentSongs
import com.example.chameleody.activity.PlayerActivity
import kotlin.random.Random

class MusicService : Service(), MediaPlayer.OnCompletionListener {
    private var mBinder: IBinder = MyBinder()
    lateinit var mediaPlayer: MediaPlayer
    lateinit var mediaSessionCompat: MediaSessionCompat
    val duration get() = mediaPlayer.duration
    val currentPosition get() = mediaPlayer.currentPosition
    val isPlaying get() = mediaPlayer.isPlaying
    private lateinit var uri : Uri

    override fun onCreate() {
        super.onCreate()
        mediaSessionCompat = MediaSessionCompat(baseContext, "My Audio")
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
        if (isPlaying) mediaPlayer.pause()
        else mediaPlayer.start()
    }

    fun nextBtnClicked(){
        mediaPlayer.stop()
        mediaPlayer.release()
        when(currentShuffle) {
            REPEAT_ALL -> currentSongPos = ((currentSongPos + 1) % currentSongs.size)
            SHUFFLE_ALL -> currentSongPos = Random.nextInt(0,currentSongs.size)
            SHUFFLE_SMART -> TODO()
        }
        createMediaPlayer(currentSongPos)
        onCompleted()
        mediaPlayer.start()
    }

    fun prevBtnClicked(){
        mediaPlayer.stop()
        mediaPlayer.release()
        when(currentShuffle) {
            REPEAT_ALL -> currentSongPos = if(currentSongPos==0) currentSongs.size-1
                                            else currentSongPos -1
            SHUFFLE_ALL -> currentSongPos = Random.nextInt(0,currentSongs.size)
            SHUFFLE_SMART -> TODO()
        }
        createMediaPlayer(currentSongPos)
        onCompleted()
        mediaPlayer.start()
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
        mediaPlayer.start()
        onCompleted()
    }

    fun showNotification(playPauseBtn : Int){
        val intent = Intent(this, PlayerActivity::class.java)
        val contentIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val prevIntent = Intent(this, PlayerActivity::class.java).setAction(ApplicationClass.ACTION_PREVIOUS)
        val prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val pauseIntent = Intent(this, PlayerActivity::class.java).setAction(ApplicationClass.ACTION_PLAY)
        val pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val nextIntent = Intent(this, PlayerActivity::class.java).setAction(ApplicationClass.ACTION_NEXT)
        val nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val picture = getAlbumArt(currentSongs[currentSongPos].path)
        val thumb = if (picture!=null){
            BitmapFactory.decodeByteArray(picture, 0, picture.size) }
        else{
            BitmapFactory.decodeResource(resources, R.drawable.default_art)
        }
        val notification = NotificationCompat.Builder(this, ApplicationClass.CHANNEL_ID_2).
        setSmallIcon(playPauseBtn).setLargeIcon(thumb).
        setContentTitle(currentSongs[currentSongPos].title).setContentText(currentSongs[currentSongPos].artist).
        addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPending).
        addAction(playPauseBtn, "Pause", pausePending).
        addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPending).
        setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.sessionToken)).
        setPriority(NotificationCompat.PRIORITY_HIGH).
        setOnlyAlertOnce(true).
        build()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}