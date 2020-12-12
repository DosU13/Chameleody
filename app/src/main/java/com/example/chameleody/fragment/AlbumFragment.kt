package com.example.chameleody.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chameleody.R
import com.example.chameleody.activity.MainActivity.Companion.albums
import com.example.chameleody.adapter.AlbumAdapter

class AlbumFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view  = inflater.inflate(R.layout.fragment_album, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        if (albums.size >= 1) {
            val albumAdapter = AlbumAdapter(context,  albums)
            recyclerView.adapter = albumAdapter
            recyclerView.layoutManager = GridLayoutManager(context,2)
        }
        return view
    }
}