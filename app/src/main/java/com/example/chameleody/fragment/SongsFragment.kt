package com.example.chameleody.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chameleody.FilesManager
import com.example.chameleody.R
import com.example.chameleody.adapter.SongsAdapter

class SongsFragment : Fragment() {
    companion object{
        lateinit var songsAdapter: SongsAdapter
    }
    private val fm = FilesManager.instance

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view  = inflater.inflate(R.layout.fragment_songs, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        if (fm.spMusicFiles.size >= 1) {
            songsAdapter = SongsAdapter(context, fm.spMusicFiles)
            recyclerView.adapter = songsAdapter
            recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        return view
    }
}