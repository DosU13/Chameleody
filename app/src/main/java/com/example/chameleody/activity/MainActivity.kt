package com.example.dreamplayer.activity

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.dreamplayer.R
import com.example.dreamplayer.fragment.AlbumFragment
import com.example.dreamplayer.fragment.SongsFragment
import com.example.dreamplayer.model.MusicFiles
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener{
    val REQUEST_CODE = 1
    val MY_SORT_PREF = "SortOrder"
    companion object{
        var musicFiles = ArrayList<MusicFiles>()
        var shuffleBoolean = false
        var repeatBoolean = false
        var albums = ArrayList<MusicFiles>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permission()
    }

    private fun permission(){
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
        }
        else {
            musicFiles = getAllAudio(this)
            initViewPager()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                musicFiles = getAllAudio(this)
                initViewPager()
            }
            else{
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
            }
        }
    }

    private fun initViewPager() {
        val viewPager = findViewById<ViewPager>(R.id.viewpager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragments(SongsFragment(), "Songs")
        viewPagerAdapter.addFragments(AlbumFragment(), "Albums")
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
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

    private fun getAllAudio(context : Context) : ArrayList<MusicFiles> {
        val preferences = getSharedPreferences(MY_SORT_PREF, Context.MODE_PRIVATE)
        val sortOrder = preferences.getString("sorting", "sortByDate")
        val duplicate = ArrayList<String>()
        albums.clear()
        val tempAudioList = ArrayList<MusicFiles>()
        var order = MediaStore.MediaColumns.DATE_ADDED + " ASC"
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        when(sortOrder){
            "sortByName"-> order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC"
            "sortByDate"-> order = MediaStore.MediaColumns.DATE_ADDED + " ASC"
            "sortBySize"-> order = MediaStore.MediaColumns.SIZE + " DESC"
        }
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID
        )
        val cursor = context.contentResolver.query(uri, projection, null, null, order)
        if (cursor!=null){
            while(cursor.moveToNext()){
                val album = cursor.getString(0)
                val title = cursor.getString(1)
                val duration = cursor.getString(2)
                val path = cursor.getString(3)
                val artist = cursor.getString(4)
                val id = cursor.getString(5)

                val musicFiles = MusicFiles(
                    path,
                    title,
                    artist,
                    album,
                    duration,
                    id
                )
                tempAudioList.add(musicFiles)
                if (!duplicate.contains(album)){
                    albums.add(musicFiles)
                    duplicate.add(album)
                }
            }
            cursor.close()
        }
        return tempAudioList
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val menuItem = menu?.findItem(R.id.search_option)
        val searchView : SearchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val userInput = newText?.toLowerCase()
        val myFiles = ArrayList<MusicFiles>()
        for (song : MusicFiles in musicFiles){
            if (song.title.toLowerCase().contains(userInput.toString()) ||
                song.artist.toLowerCase().contains(userInput.toString()) ||
                song.album.toLowerCase().contains(userInput.toString())){
                myFiles.add(song)
            }
        }
        SongsFragment.musicAdapter.updateList(myFiles)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val editor = getSharedPreferences(MY_SORT_PREF, Context.MODE_PRIVATE).edit()
        when (item.itemId){
            R.id.by_name -> {
                editor.putString("sorting", "sortByName")
                editor.apply()
                this.recreate()}
            R.id.by_date -> {
                editor.putString("sorting", "sortByDate")
                editor.apply()
                this.recreate()}
            R.id.by_size -> {
                editor.putString("sorting", "sortBySize")
                editor.apply()
                this.recreate()}
        }
        return super.onOptionsItemSelected(item)
    }
}