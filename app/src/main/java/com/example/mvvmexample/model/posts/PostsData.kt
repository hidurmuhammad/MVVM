package com.example.mvvmexample.model.posts

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "posts")
@Parcelize
data class PostsData(
    @PrimaryKey(autoGenerate = true) var post_id:Int,
    val body: String,
    val id: Int,
    val title: String,
    val userId: Int
):Parcelable