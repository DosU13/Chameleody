package com.example.chameleody.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.chameleody.MusicService
import com.example.chameleody.R
import com.example.chameleody.activity.MainActivity.Companion.currentSongPos
import com.example.chameleody.activity.MainActivity.Companion.currentSongs
import com.example.chameleody.activity.PlayerActivity
import com.example.chameleody.adapter.SongsAdapter
import com.example.chameleody.model.MusicFiles
import kotlinx.android.synthetic.main.activity_player.*

class PlayerFragment : Fragment(), ServiceConnection{
    lateinit var musicService: MusicService
    lateinit var playerLayout: LinearLayout
    lateinit var art: ImageView
    lateinit var name: TextView
    lateinit var artist: TextView
    lateinit var prevBtn: Button
    lateinit var playBtn: Button
    lateinit var nextBtn: Button

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       return inflater.inflate(R.layout.fragment_player, container, false)
   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        if (currentSongPos < currentSongs.size) {
            val currentSong: MusicFiles = currentSongs[currentSongPos]
            val image = getAlbumArt(currentSong.path)
            if (image != null) {
                activity?.let { Glide.with(it).asBitmap().load(image).into(art) }
            } else {
                activity?.let { Glide.with(it).load(R.drawable.default_art).into(art) }
            }
            name.text = currentSong.title
            artist.text = currentSong.artist
        }
    }

    override fun onResume() {
        val intent = Intent(activity, MusicService::class.java)
        activity?.bindService(intent, this, Context.BIND_AUTO_CREATE)
        playBtn.setOnClickListener {playPauseBtnClicked()}
        prevBtn.setOnClickListener { prevBtnClicked() }
        nextBtn.setOnClickListener{ nextBtnClicked()}
        playerLayout.setOnClickListener {
            val intent = Intent(activity, PlayerActivity::class.java)
            activity?.startActivity(intent)
        }
        super.onResume()
    }

    private fun initViews(view: View) {
        playerLayout = view.findViewById(R.id.player_fr)
        art = view.findViewById(R.id.art_player_fr)
        name = view.findViewById(R.id.track_name_player_fr)
        artist = view.findViewById(R.id.track_artist_player_fr)
        prevBtn = view.findViewById(R.id.previous_player_fr)
        playBtn = view.findViewById(R.id.play_player_fr)
        nextBtn = view.findViewById(R.id.next_player_fr)
    }

    private fun setViews(){
        val image = getAlbumArt(currentSongs[currentSongPos].path)
        if (image != null) activity?.let { Glide.with(it).asBitmap().load(image).into(art) }
        else activity?.let {Glide.with(it).load(R.drawable.default_art).into(art)}
        name.text = currentSongs[currentSongPos].title
        artist.text = currentSongs[currentSongPos].artist
        if (musicService.isPlaying){
            musicService.showNotification(R.drawable.ic_baseline_pause_24)
            playBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        }else{
            musicService.showNotification(R.drawable.ic_baseline_play_arrow_24)
            playBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    private fun playPauseBtnClicked(){
        if (musicService.isPlaying) {
            musicService.showNotification(R.drawable.ic_baseline_play_arrow_24)
            playBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        }else{
            musicService.showNotification(R.drawable.ic_baseline_pause_24)
            playBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        }
        musicService.playPauseBtnClicked()
    }

    private fun nextBtnClicked(){
        musicService.nextBtnClicked()
        setViews()
    }

    private fun prevBtnClicked(){
        musicService.prevBtnClicked()
        setViews()
    }

    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val myBinder = service as MusicService.MyBinder
        musicService = myBinder.service
    }

    override fun onServiceDisconnected(p0: ComponentName?) {}

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}