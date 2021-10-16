package com.example.mvvmexample.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Photo")
@Parcelize
data class PhotoData(
    @PrimaryKey(autoGenerate = true) var p_id:Int,
    val albumId: Int?,
    val id: Int?,
    val thumbnailUrl: String?,
    val title: String?,
    val url: String?
):Parcelable