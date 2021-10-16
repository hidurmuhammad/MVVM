package com.example.mvvmexample.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvvmexample.model.posts.CreatePost
import com.example.mvvmexample.respository.CreatePostRepository

class CreatePostViewModel (application: Application): AndroidViewModel(application){

    private var createPostRepository:CreatePostRepository?=null
    var postModelListLiveData : LiveData<List<CreatePost>>?=null
    var createPostLiveData: LiveData<CreatePost>?=null
    var deletePostLiveData:LiveData<Boolean>?=null

    init {
        createPostRepository = CreatePostRepository()
        postModelListLiveData = MutableLiveData()
        createPostLiveData = MutableLiveData()
         deletePostLiveData = MutableLiveData()
    }

    fun fetchAllPosts(){
        postModelListLiveData = createPostRepository?.fetchAllPosts()
    }

    fun createPost(postModel: CreatePost){

        createPostLiveData = createPostRepository!!.createPost(postModel)

    }

    fun deletePost(id:Int){
         deletePostLiveData = createPostRepository?.deletePost(id)
    }

}