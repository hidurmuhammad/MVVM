package com.example.mvvmexample.views.photo.interfaces

import android.view.View
import com.example.mvvmexample.model.PhotoData

interface PhotoClickListener {
    fun onItemClick(view:View,data : PhotoData)
}