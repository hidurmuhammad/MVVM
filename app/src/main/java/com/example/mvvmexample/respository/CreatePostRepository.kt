package com.example.mvvmexample.respository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvvmexample.api.ApiClients
import com.example.mvvmexample.api.ApiService
import com.example.mvvmexample.model.posts.CreatePost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePostRepository {

    private var apiInterface:ApiService?=null

    init {
        apiInterface = ApiClients.getApiClient().create(ApiService::class.java)
    }

    fun fetchAllPosts():LiveData<List<CreatePost>>{
        val data = MutableLiveData<List<CreatePost>>()

        apiInterface?.fetchAllPosts()?.enqueue(object : Callback<List<CreatePost>>{

            override fun onFailure(call: Call<List<CreatePost>>, t: Throwable) {
                data.value = null!!
            }

            override fun onResponse(call: Call<List<CreatePost>>,
                response: Response<List<CreatePost>>) {

                val res = response.body()
                if (response.code() == 200 &&  res!=null){
                    data.value = res!!
                }else{
                    data.value = null!!
                }

            }
        })

        return data

    }

    fun createPost(postModel: CreatePost):LiveData<CreatePost>{
        val data = MutableLiveData<CreatePost>()

        apiInterface?.createPost(postModel)?.enqueue(object : Callback<CreatePost> {
            override fun onFailure(call: Call<CreatePost>, t: Throwable) {
                data.value = null!!
            }

            override fun onResponse(call: Call<CreatePost>, response: Response<CreatePost>) {
                val res = response.body()
                if (response.code() == 201 && res!=null){
                    data.value = res!!
                }else{
                    data.value = null!!
                }
            }
        })

        return data

    }

    fun deletePost(id:Int):LiveData<Boolean>{
        val data = MutableLiveData<Boolean>()

        apiInterface?.deletePost(id)?.enqueue(object : Callback<String>{
            override fun onFailure(call: Call<String>, t: Throwable) {
                data.value = false
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                data.value = response.code() == 200
            }
        })

        return data

    }

}