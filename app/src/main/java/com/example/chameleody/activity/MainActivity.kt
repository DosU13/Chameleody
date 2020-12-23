package com.example.chameleody.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.chameleody.ApplicationClass
import com.example.chameleody.MusicService
import com.example.chameleody.R
import com.example.chameleody.fragment.AlbumFragment
import com.example.chameleody.fragment.PlayerFragment
import com.example.chameleody.fragment.SongsFragment
import com.example.chameleody.model.MusicFile
import com.example.chameleody.FilesManager
import com.example.chameleody.db.MusicViewModel
import com.example.chameleody.db.MusicViewModelFactory
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(){
    companion object{
        const val REQUEST_CODE = 1
        const val MY_SORT_PREF = "SortOrder"
        const val MY_CURRENT_POS_PREF = "CurrentSongPosition"
    }
    private val fm = FilesManager.instance
    private val musicViewModel: MusicViewModel by viewModels{
        MusicViewModelFactory((application as ApplicationClass).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicViewModel.allMusics.observe(this, androidx.lifecycle.Observer { musics->
            musics?.let { fm.viewModelReady(musics) }
        })
        fm.musicViewModel = musicViewModel
        setContentView(R.layout.activity_main)
        permission()
    }

    private fun permission(){
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Companion.REQUEST_CODE)
        }
        else {
            initAll()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Companion.REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                initAll()
            }
            else{
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Companion.REQUEST_CODE
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initPlayerFr()
        val str: CharSequence = "DB: "+ musicViewModel.allMusics.value?.size+"<->"
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }

    private fun initAll(){
        fm.getAllAudio(this)
        initViews()
        initViewPager()
        initPlayerFr()
        initService()
    }

    private fun initViews(){
//        val optionsBtn : Button = findViewById(R.id.options_main)
        val searchBar : SearchView = findViewById(R.id.search_bar_main)
//        val recentBtn : Button = findViewById(R.id.recent)
//        val favoriteBtn : Button = findViewById(R.id.favorite)
//        val playlistBtn : Button = findViewById(R.id.play_list)
//        val shuffleBtn : Button = findViewById(R.id.shuffle_all_main)
//        val smartShuffle : LinearLayout = findViewById(R.id.adv_shuffle_all_main)
        val sortOptions : Button = findViewById(R.id.sort_options_main)
//        val showOptions : Button = findViewById(R.id.show_by_main)

        searchBar.setOnQueryTextListener(object: OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val userInput = newText?.toLowerCase(Locale.ROOT)
                val myFiles = ArrayList<MusicFile>()
                for (song : MusicFile in fm.spMusicFiles) {
                    if (song.name.toLowerCase(Locale.ROOT).contains(userInput.toString()) ||
                        song.artist.toLowerCase(Locale.ROOT).contains(userInput.toString()) ||
                        song.album.toLowerCase(Locale.ROOT).contains(userInput.toString())){
                        myFiles.add(song)
                    }
                }
                SongsFragment.songsAdapter.updateList(myFiles)
                return true
            }

        })
        sortOptions.setOnClickListener{v->
            val popupMenu = PopupMenu(this, v)
            popupMenu.menuInflater.inflate(R.menu.popup_sort_opt, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
                PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    val editor = getSharedPreferences(MY_SORT_PREF, Context.MODE_PRIVATE).edit()
                    when (item?.itemId){
                        R.id.by_name -> {
                            editor.putString("sorting", "sortByName")
                            editor.apply()}
                        R.id.by_date -> {
                            editor.putString("sorting", "sortByDate")
                            editor.apply()}
                        R.id.by_size -> {
                            editor.putString("sorting", "sortBySize")
                            editor.apply()}
                    }
                    return true
                }
            })
        }
    }

    private fun initService(){
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("servicePosition", -1)
        startService(intent)
    }

    private fun initPlayerFr() {
        val prefs = getSharedPreferences(MY_CURRENT_POS_PREF, MODE_PRIVATE)
        val bundle = Bundle()
        val playerFr = PlayerFragment()
        playerFr.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.player_fr_main, playerFr)
        ft.commit()
    }

    private fun initViewPager() {
        val viewPager = findViewById<ViewPager>(R.id.viewpager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragments(SongsFragment(), "Songs")
        viewPagerAdapter.addFragments(AlbumFragment(), "Albums")
        viewPager.adapter = viewPagerAdapter
    }

    class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private var fragments = ArrayList<Fragment>()
        private var titles = ArrayList<String>()

        fun addFragments(fragment: Fragment, title: String){
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return  fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
}