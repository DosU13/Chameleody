package com.example.chameleody.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.chameleody.MusicService
import com.example.chameleody.R
import com.example.chameleody.activity.MainActivity.Companion.REPEAT_ALL
import com.example.chameleody.activity.MainActivity.Companion.REPEAT_ONE
import com.example.chameleody.activity.MainActivity.Companion.SHUFFLE_ALL
import com.example.chameleody.activity.MainActivity.Companion.SHUFFLE_SMART
import com.example.chameleody.activity.MainActivity.Companion.currentShuffle
import com.example.chameleody.activity.MainActivity.Companion.currentSongPos
import com.example.chameleody.activity.MainActivity.Companion.currentSongs
import com.example.chameleody.adapter.AlbumDetailsAdapter.Companion.albumFiles
import com.example.chameleody.adapter.SongsAdapter.Companion.mFiles
import com.example.chameleody.fragment.CoverArtFragment
import com.example.chameleody.fragment.GeneralInfoFragment
import com.example.chameleody.fragment.LyricsFragment
import com.example.chameleody.fragment.MoodFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity(), ServiceConnection{
    private lateinit var songName : TextView
    private lateinit var artistName : TextView
    private lateinit var durationPlayed : TextView
    private lateinit var durationTotal : TextView
    private lateinit var nextBtn : ImageView
    private lateinit var prevBtn : ImageView
    private lateinit var backBtn : ImageView
    private lateinit var shuffleBtn : ImageView
    private lateinit var repeatBtn : ImageView
    private lateinit var playPauseBtn : Button
    private lateinit var seekBar : SeekBar
    private lateinit var musicService: MusicService
    private lateinit var uri : Uri
    private lateinit var handler : Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initViews()
        getIntentMethod()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBarNotMy: SeekBar?, progress: Int, fromUser: Boolean) {
                if (::musicService.isInitialized && fromUser){
                    musicService.seekTo(progress * 1000)
                }
                val mCurrentPosition = musicService.currentPosition/1000
                seekBar.progress = mCurrentPosition
                durationPlayed.text = formattedTime(mCurrentPosition)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        handler = Handler(Looper.getMainLooper())
        this@PlayerActivity.runOnUiThread(runnable {
            if (::musicService.isInitialized){
                val mCurrentPosition = musicService.currentPosition/1000
                seekBar.progress = mCurrentPosition
                durationPlayed.text = formattedTime(mCurrentPosition)
            }
            handler.postDelayed(this, 1000)
        })
        shuffleBtn.setOnClickListener {
            if (currentShuffle == 4) currentShuffle = 1
            else currentShuffle++
            when (currentShuffle) {
                REPEAT_ONE -> shuffleBtn.setImageResource(R.drawable.repeat_one)
                REPEAT_ALL -> shuffleBtn.setImageResource(R.drawable.repeat_all)
                SHUFFLE_ALL -> shuffleBtn.setImageResource(R.drawable.shuffle_all)
                SHUFFLE_SMART -> shuffleBtn.setImageResource(R.drawable.shuffle_smart)
            }
        }
        repeatBtn.setOnClickListener {
            TODO()
        }
        backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
        playPauseBtn.setOnClickListener {playPauseBtnClicked()}
        prevBtn.setOnClickListener { prevBtnClicked() }
        nextBtn.setOnClickListener{ nextBtnClicked()}
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        unbindService(this)
    }

    private fun nextBtnClicked() {
        musicService.nextBtnClicked()
        uri = Uri.parse(currentSongs[currentSongPos].path)
        metaData(uri)
        songName.text = currentSongs[currentSongPos].title
        artistName.text = currentSongs[currentSongPos].artist
        initSeekThread()
        musicService.showNotification(R.drawable.ic_baseline_pause_24)
        playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
    }

    private fun prevBtnClicked() {
        musicService.prevBtnClicked()
        uri = Uri.parse(currentSongs[currentSongPos].path)
        metaData(uri)
        songName.text = currentSongs[currentSongPos].title
        artistName.text = currentSongs[currentSongPos].artist
        initSeekThread()
        musicService.showNotification(R.drawable.ic_baseline_pause_24)
        playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
    }

    private fun playPauseBtnClicked() {
        if (musicService.isPlaying){
            musicService.showNotification(R.drawable.ic_baseline_play_arrow_24)
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        }
        else{
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            musicService.showNotification(R.drawable.ic_baseline_pause_24)
        }
        musicService.playPauseBtnClicked()
        initSeekThread()
    }

    private fun initSeekThread(){
        seekBar.max = musicService.duration / 1000
        this@PlayerActivity.runOnUiThread(runnable {
            if (::musicService.isInitialized){
                val mCurrentPosition = musicService.currentPosition/1000
                seekBar.progress = mCurrentPosition
            }
            handler.postDelayed(this, 1000)
        })
    }

    private fun formattedTime(mCurrentPosition: Int): String {
        val seconds : String = (mCurrentPosition % 60).toString()
        val minutes : String = (mCurrentPosition / 60).toString()
        val totalOut = "$minutes:$seconds"
        val totalNew = "$minutes:0$seconds"
        return if (seconds.length == 1){
            totalNew
        } else {
            totalOut
        }
    }

    private fun getIntentMethod() {
        currentSongPos = intent.getIntExtra("position", 0)
        val sender = intent.getStringExtra("sender")
        currentSongs = if (sender != null && sender == "albumDetails") albumFiles //currentSongs must be already changed to album files from albumAdapter
                    else mFiles
        playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        uri = Uri.parse(currentSongs[currentSongPos].path)
    }

    private fun initViews() {
        songName = findViewById(R.id.song_name)
        artistName = findViewById(R.id.song_artist)
        durationPlayed = findViewById(R.id.durationPlayed)
        durationTotal = findViewById(R.id.durationTotal)
        nextBtn = findViewById(R.id.id_next)
        prevBtn = findViewById(R.id.id_prev)
        backBtn = findViewById(R.id.back_btn)
        shuffleBtn = findViewById(R.id.id_shuffle)
        repeatBtn = findViewById(R.id.id_repeat)
        playPauseBtn = findViewById(R.id.play_pause)
        seekBar = findViewById(R.id.seekbar)

        val viewPager: ViewPager = findViewById(R.id.viewpager_player)
        val tabLayout: TabLayout = findViewById(R.id.tabDots)
        tabLayout.setupWithViewPager(viewPager, true)
        val viewPagerAdapter = MainActivity.ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragments(LyricsFragment(), "")
        viewPagerAdapter.addFragments(CoverArtFragment(), "")
        viewPagerAdapter.addFragments(GeneralInfoFragment(), "")
        viewPagerAdapter.addFragments(MoodFragment(), "")
        viewPager.adapter = viewPagerAdapter
    }

    private fun metaData(uri: Uri){
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        val durationTotalInt = Integer.parseInt(currentSongs[currentSongPos].duration) / 1000
        durationTotal.text = formattedTime(durationTotalInt)
        val art : ByteArray? = retriever.embeddedPicture
        val bitmap : Bitmap?
        if (art != null){
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
            if (bitmap != null) {
                Palette.from(bitmap).generate(Palette.PaletteAsyncListener {
                    val swatch = it?.dominantSwatch
                    val mContainer = findViewById<LinearLayout>(R.id.mContainer)
                    mContainer.setBackgroundResource(R.drawable.main_bg)
                    if (swatch != null) {
                        val gradientDrawableBg = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(swatch.rgb, swatch.rgb)
                        )
                        mContainer.background = gradientDrawableBg
                        songName.setTextColor(swatch.titleTextColor)
                        artistName.setTextColor(swatch.bodyTextColor)
                    } else {
                        val gradientDrawableBg = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(0xff000000.toInt(), 0xff000000.toInt())
                        )
                        mContainer.background = gradientDrawableBg
                        songName.setTextColor(Color.WHITE)
                        artistName.setTextColor(Color.WHITE)
                    }
                })
            }
            else{
                val mContainer = findViewById<LinearLayout>(R.id.mContainer)
                mContainer.setBackgroundResource(R.drawable.main_bg)
                songName.setTextColor(Color.WHITE)
                artistName.setTextColor(Color.WHITE)
            }
        }
        else {
            val mContainer = findViewById<LinearLayout>(R.id.mContainer)
            mContainer.setBackgroundResource(R.drawable.main_bg)
            songName.setTextColor(Color.WHITE)
            artistName.setTextColor(Color.WHITE)
        }
    }

    private fun runnable(body: Runnable.(Runnable)->Unit) = object : Runnable{
        override fun run() {
            this.body(this)
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val myBinder = service as MusicService.MyBinder
        musicService = myBinder.service
        musicService.playMedia(currentSongPos)
        seekBar.max = musicService.duration / 1000
        metaData(uri)
        songName.text = currentSongs[currentSongPos].title
        artistName.text = currentSongs[currentSongPos].artist
        musicService.onCompleted()
        musicService.showNotification(R.drawable.ic_baseline_pause_24)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        //musicService = null
    }
}