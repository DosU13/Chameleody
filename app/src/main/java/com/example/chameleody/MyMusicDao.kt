package com.example.chameleody

import androidx.room.*
import com.example.chameleody.model.MyMusic

@Dao
interface MyMusicDao {
    @Query("SELECT * FROM MyMusic")
    fun getAll(): List<MyMusic>

    @Insert
    fun insert(myMusic: MyMusic)

    @Delete
    fun delete(myMusic: MyMusic)

    @Update
    fun update(myMusic: MyMusic)
}