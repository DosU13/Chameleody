package com.example.chameleody.fragment

import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.chameleody.R
import com.example.chameleody.activity.MainActivity.Companion.currentSongPos
import com.example.chameleody.activity.MainActivity.Companion.currentSongs
import com.example.chameleody.model.MusicFiles

class PlayerFragment : Fragment() {
   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       return inflater.inflate(R.layout.fragment_player, container, false)
   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val playerLayout = view.findViewById<LinearLayout>(R.id.player_fr)
        val art = view.findViewById<ImageView>(R.id.art_player_fr)
        val name = view.findViewById<TextView>(R.id.track_name_player_fr)
        val artist = view.findViewById<TextView>(R.id.track_artist_player_fr)
        val previous = view.findViewById<Button>(R.id.previous_player_fr)
        val play = view.findViewById<Button>(R.id.play_player_fr)
        val next = view.findViewById<Button>(R.id.next_player_fr)
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
        init()
    }

    fun init() {

    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}