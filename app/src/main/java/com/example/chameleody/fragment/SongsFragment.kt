package com.example.dreamplayer.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamplayer.adapter.MusicAdapter
import com.example.dreamplayer.R
import com.example.dreamplayer.activity.MainActivity.Companion.musicFiles
import com.example.dreamplayer.activity.PlayerActivity
import com.example.dreamplayer.adapter.MusicAdapter.Companion.mFiles

class SongsFragment : Fragment() {
    companion object{
        lateinit var musicAdapter: MusicAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view  = inflater.inflate(R.layout.fragment_songs, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        if (musicFiles.size >= 1) {
            musicAdapter = MusicAdapter(context, musicFiles)
            recyclerView.adapter = musicAdapter
            recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        return view
    }
}