package com.example.mvvmexample.model.todos

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Todos")
@Parcelize
data class TodosData(
    @PrimaryKey(autoGenerate = true) var t_id:Int,
    val completed: Boolean?,
    val id: Int?,
    val title: String?,
    val userId: Int?
):Parcelable