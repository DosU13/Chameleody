package com.example.chameleody.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class MusicFiles (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name="path")
    val path : String,
    @ColumnInfo(name="name")
    val name : String,
    @ColumnInfo(name="artist")
    val artist : String,
    @ColumnInfo(name="album")
    val album : String,
    @ColumnInfo(name="duration")
    val duration : String,
    @ColumnInfo(name="sp_id")
    val sp_id : String,
    @ColumnInfo(name="date_added")
    val dateAdded: String,
    @ColumnInfo(name="genre")
    val genre: Int,
    @ColumnInfo(name="version")
    val version: Int,
    @ColumnInfo(name="mood")
    val mood: Int
) : Serializable {
    constructor(path: String, title: String, artist: String, album: String, duration: String, sp_id: String, dateAdded: String):
        this(0, path, title, artist, album, duration, sp_id, dateAdded, 0, 0, 0)
}

