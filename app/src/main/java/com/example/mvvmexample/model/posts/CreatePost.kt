package com.example.mvvmexample.model.posts

import com.google.gson.annotations.SerializedName

data class CreatePost (
    var userId:Int?=0,
    var id:Int?=0,
    var title:String?="",
    var body:String?=""

        )