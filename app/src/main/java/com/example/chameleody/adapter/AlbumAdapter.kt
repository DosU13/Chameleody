package com.example.dreamplayer.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dreamplayer.activity.AlbumDetails
import com.example.dreamplayer.R
import com.example.dreamplayer.model.MusicFiles

class AlbumAdapter(private val mContext: Context?, private val albumFiles: ArrayList<MusicFiles>) :
    RecyclerView.Adapter<AlbumAdapter.MyViewHolderAlbum>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderAlbum {
        val view = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false)
        return MyViewHolderAlbum(view)
    }

    override fun onBindViewHolder(holder: MyViewHolderAlbum, position: Int) {
        holder.albumName.text = albumFiles[position].album
        val image = getAlbumArt(albumFiles[position].path)
        if (mContext != null) {
            if (image != null) {
                Glide.with(mContext).asBitmap().load(image).into(holder.albumImage)
            } else {
                Glide.with(mContext).load(R.drawable.default_art)
                    .into(holder.albumImage)
            }
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, AlbumDetails::class.java)
            intent.putExtra("albumName", albumFiles[position].album)
            mContext?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return albumFiles.size
    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }

    class MyViewHolderAlbum(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumName = itemView.findViewById<TextView>(R.id.album_name)
        val albumImage = itemView.findViewById<ImageView>(R.id.album_image)
    }
}