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
import android.os.*
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import androidx.viewpager.widget.ViewPager
import com.example.chameleody.FilesManager
import com.example.chameleody.MusicService
import com.example.chameleody.R
import com.example.chameleody.MusicService.Companion.REPEAT_ALL
import com.example.chameleody.MusicService.Companion.REPEAT_ONE
import com.example.chameleody.MusicService.Companion.SHUFFLE_ALL
import com.example.chameleody.MusicService.Companion.SHUFFLE_SMART
import com.example.chameleody.fragment.CoverArtFragment
import com.example.chameleody.fragment.GeneralInfoFragment
import com.example.chameleody.fragment.LyricsFragment
import com.example.chameleody.fragment.MoodFragment
import com.google.android.material.tabs.TabLayout

class PlayerActivity : AppCompatActivity(), ServiceConnection{
    private lateinit var songName : TextView
    private lateinit var artistName : TextView
    private lateinit var durationPlayed : TextView
    private lateinit var durationTotal : TextView
    private lateinit var nextBtn : ImageView
    private lateinit var prevBtn : ImageView
    private lateinit var backBtn : ImageView
    private lateinit var shuffleBtn : ImageView
    private lateinit var smartShuffleBtn : ImageView
    private lateinit var playPauseBtn : Button
    private lateinit var seekBar : SeekBar
    private lateinit var musicService: MusicService
    private lateinit var uri : Uri
    private lateinit var handler : Handler
    private var startNew = false
    private val fm = FilesManager.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        uri = Uri.parse(fm.currentSong.path)
        startNew = intent.getBooleanExtra("new", true)
        initViews()
        handler = Handler(Looper.getMainLooper())
        initListeners()
        //getIntentMethod()
    }

    override fun onResume() {
        val intent = Intent(this, MusicService::class.java)
        applicationContext.bindService(intent, this, Context.BIND_AUTO_CREATE)
        refreshViews()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        applicationContext.unbindService(this)
    }

    private fun initListeners(){
        playPauseBtn.setOnClickListener {playPauseBtnClicked()}
        prevBtn.setOnClickListener { prevBtnClicked() }
        nextBtn.setOnClickListener{ nextBtnClicked()}
        shuffleBtn.setOnClickListener {
            if (fm.currentShuffle == 4) fm.currentShuffle = 1
            else fm.currentShuffle++
            shuffleBtn.setImageResource(when(fm.currentShuffle) {
                REPEAT_ONE -> R.drawable.repeat_one
                REPEAT_ALL -> R.drawable.repeat_all
                SHUFFLE_ALL -> R.drawable.shuffle_all
                SHUFFLE_SMART -> R.drawable.shuffle_smart
                else -> R.drawable.repeat_all
            })
        }
        smartShuffleBtn.setOnClickListener {

        }
        backBtn.setOnClickListener {
            finish()
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBarNotMy: SeekBar?, progress: Int, fromUser: Boolean) {
                if (::musicService.isInitialized && fromUser){
                    musicService.seekTo(progress * 1000)
                    val mCurrentPosition = musicService.currentPosition/1000
                    seekBar.progress = mCurrentPosition
                    durationPlayed.text = formattedTime(mCurrentPosition)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        this@PlayerActivity.runOnUiThread(runnable {
            if (::musicService.isInitialized){
                val mCurrentPosition = musicService.currentPosition/1000
                seekBar.progress = mCurrentPosition
                durationPlayed.text = formattedTime(mCurrentPosition)
            }
            handler.postDelayed(this, 1000)
        })
    }

    private fun refreshViews(){
        if(this::musicService.isInitialized){
            playPauseBtn.setBackgroundResource(
                if(musicService.isPlaying) R.drawable.ic_baseline_pause_24
                else R.drawable.ic_baseline_play_arrow_24)
            seekBar.max = musicService.duration / 1000
            val mCurrentPosition = musicService.currentPosition/1000
            seekBar.progress = mCurrentPosition
            durationPlayed.text = formattedTime(mCurrentPosition)
        }
        shuffleBtn.setImageResource(when(fm.currentShuffle) {
            REPEAT_ONE -> R.drawable.repeat_one
            REPEAT_ALL -> R.drawable.repeat_all
            SHUFFLE_ALL -> R.drawable.shuffle_all
            SHUFFLE_SMART -> R.drawable.shuffle_smart
            else -> R.drawable.repeat_all
        })
        songName.text = fm.currentSong.name
        artistName.text = fm.currentSong.artist
        uri = Uri.parse(fm.currentSong.path)
        metaData(uri)
        lyricsFragment.refreshViews()
        coverArtFragment.refreshViews()
        infoFragment.refreshViews()
        moodFragment.refreshViews()
    }

    private fun nextBtnClicked() {
        musicService.nextBtnClicked()
        refreshViews()
        musicService.showNotification(R.drawable.ic_baseline_pause_24)
    }

    private fun prevBtnClicked() {
        musicService.prevBtnClicked()
        refreshViews()
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

    lateinit var lyricsFragment: LyricsFragment
    lateinit var coverArtFragment: CoverArtFragment
    lateinit var infoFragment: GeneralInfoFragment
    lateinit var moodFragment: MoodFragment
    private fun initViews() {
        songName = findViewById(R.id.song_name)
        artistName = findViewById(R.id.song_artist)
        durationPlayed = findViewById(R.id.durationPlayed)
        durationTotal = findViewById(R.id.durationTotal)
        nextBtn = findViewById(R.id.id_next)
        prevBtn = findViewById(R.id.id_prev)
        backBtn = findViewById(R.id.back_btn)
        shuffleBtn = findViewById(R.id.id_shuffle)
        smartShuffleBtn = findViewById(R.id.id_smart_shuffle)
        playPauseBtn = findViewById(R.id.play_pause)
        seekBar = findViewById(R.id.seekbar)

        val viewPager: ViewPager = findViewById(R.id.viewpager_player)
        val tabLayout: TabLayout = findViewById(R.id.tabDots)
        tabLayout.setupWithViewPager(viewPager, true)
        val viewPagerAdapter = MainActivity.ViewPagerAdapter(supportFragmentManager)
        lyricsFragment = LyricsFragment()
        coverArtFragment = CoverArtFragment()
        infoFragment = GeneralInfoFragment()
        moodFragment = MoodFragment()
        viewPagerAdapter.addFragments(lyricsFragment, "")
        viewPagerAdapter.addFragments(coverArtFragment, "")
        viewPagerAdapter.addFragments(infoFragment, "")
        viewPagerAdapter.addFragments(moodFragment, "")
        viewPager.adapter = viewPagerAdapter
        viewPager.currentItem = 1
    }

    private fun metaData(uri: Uri){
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        val durationTotalInt = Integer.parseInt(fm.currentSong.duration) / 1000
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
        if(startNew) musicService.playMedia(fm.currentSongPos)
        startNew = false
        musicService.initListener()
        musicService.showNotification(R.drawable.ic_baseline_pause_24)
        refreshViews()
        val serviceMsg = musicService.messenger
        try {
            val msg = Message.obtain(null, MusicService.MSG_REGISTER_CLIENT)
            msg.replyTo = messenger
            serviceMsg.send(msg)
        }catch (ignore: RemoteException){}
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        //musicService = null
    }

    private val messenger = Messenger(IncomingHandler())

    inner class IncomingHandler: Handler(){
        override fun handleMessage(msg: Message) {
            if(msg.what == MusicService.MSG_COMPLETED){
                refreshViews()
            }
            else super.handleMessage(msg)
        }
    }
}