package com.example.chameleody.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.palette.graphics.Palette
import com.example.chameleody.FilesManager
import com.example.chameleody.R
import java.text.SimpleDateFormat
import java.util.*

class GeneralInfoFragment : Fragment() {
    private val fc = FilesManager.instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.fragment_general_info, container, false)
        return inflater.inflate(R.layout.fragment_general_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshViews()
    }

    fun refreshViews() {
        val song = fc.currentSong
        val name = view?.findViewById<TextView>(R.id.track_name_info)
        val artist = view?.findViewById<TextView>(R.id.artist_info)
        val album = view?.findViewById<TextView>(R.id.album_info)
        val genre = view?.findViewById<Spinner>(R.id.genre_info)
        val age = view?.findViewById<Spinner>(R.id.age_info)
        val version = view?.findViewById<Spinner>(R.id.version_info)
        name?.text = song.name
        artist?.text = song.artist
        album?.text = song.album
        genre?.setSelection(song.genre)
        age?.setSelection(fc.age)
        version?.setSelection(song.version)
        genre?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                song.genre = p2
                fc.updateToDB()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        age?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                fc.age = p2
                fc.updateToDB()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        version?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                song.version = p2
                fc.updateToDB()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun getSwatch(): Palette.Swatch?{
        var swatch: Palette.Swatch? = null
        val uri = FilesManager.instance.currentSong.path
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art : ByteArray? = retriever.embeddedPicture
        val bitmap : Bitmap?
        if (art != null){
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
            if (bitmap != null) {
                Palette.from(bitmap).generate(Palette.PaletteAsyncListener {
                    swatch = it?.dominantSwatch
                })
            }
        }
        return swatch
    }
}