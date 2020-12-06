package com.example.dreamplayer.adapter

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dreamplayer.R
import com.example.dreamplayer.activity.PlayerActivity
import com.example.dreamplayer.model.MusicFiles
import com.google.android.material.snackbar.Snackbar
import java.io.File

class MusicAdapter(private val mContext: Context?, private val mFilesVal: ArrayList<MusicFiles>) :
    RecyclerView.Adapter<MusicAdapter.MyViewHolder>(){
    companion object{
        lateinit var mFiles : ArrayList<MusicFiles>
    }

    init {
        mFiles = mFilesVal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.fileName.text = mFiles[position].title
        val image = getAlbumArt(mFiles[position].path)
        if (mContext != null) {
            if (image != null) {
                Glide.with(mContext).asBitmap().load(image).into(holder.albumArt)
            } else {
                Glide.with(mContext).load(R.drawable.default_art)
                    .into(holder.albumArt)
            }
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, PlayerActivity::class.java)
            intent.putExtra("position", position)
            mContext?.startActivity(intent)
        }
        holder.menuMore.setOnClickListener { v ->
            val popupMenu = PopupMenu(mContext, v)
            popupMenu.menuInflater.inflate(R.menu.popup, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
                PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(it: MenuItem?): Boolean {
                    if (it?.itemId  == R.id.delete) {
                        Toast.makeText(mContext, "Delete Clicked!", Toast.LENGTH_SHORT).show()
                        deleteFile(position, v)
                    }
                    return true
                }

            })
        }
    }

    private fun deleteFile(position: Int, v: View?) {
        val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mFiles[position].id.toLong())
        val file = File(mFiles[position].id)
        val deleted = file.delete()
        if (deleted) {
            mContext?.contentResolver?.delete(contentUri,null,null)
            mFiles.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, mFiles.size)
            Snackbar.make(v!!, "File Deleted : ", Snackbar.LENGTH_LONG).show()
        }
        else{
            Snackbar.make(v!!, "File can't be Deleted : ", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return mFiles.size
    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }

    fun updateList(musicFilesArrayList: ArrayList<MusicFiles>){
        mFiles = ArrayList<MusicFiles>()
        mFiles.addAll(musicFilesArrayList)
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileName = itemView.findViewById<TextView>(R.id.music_file_name)
        val albumArt = itemView.findViewById<ImageView>(R.id.music_img)
        val menuMore = itemView.findViewById<ImageView>(R.id.menu_more)
    }
}