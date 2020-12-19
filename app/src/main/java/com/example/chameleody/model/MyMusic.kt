package com.example.chameleody.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.io.Serializable
import java.util.*

@Entity
class MyMusic(
@PrimaryKey(autoGenerate = true)
val int: Int,
@ColumnInfo(name="name")
val name: String,
@ColumnInfo(name="artist")
val artist: Array<String>,
@ColumnInfo(name="album")
val album: String,
@ColumnInfo(name="genre")
val genre: Int,
@ColumnInfo(name="date_added")
val dateAdded: Date,
@ColumnInfo(name="version")
val version: Int,
@ColumnInfo(name="mood")
val mood: Int
) : Serializable {
}