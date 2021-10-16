package com.example.mvvmexample.viewmodel

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmexample.model.posts.PostsData
import com.example.mvvmexample.respository.PostListRepository
import com.example.mvvmexample.utils.AppResult
import com.example.mvvmexample.utils.CustomException
import com.example.mvvmexample.utils.SingleLiveEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class PostListViewModel(private val repository: PostListRepository) : ViewModel() {

    val showLoading = ObservableBoolean()
    val postList = MutableLiveData<List<PostsData>?>()
    val showError = SingleLiveEvent<String?>()

    fun getAllPosts(userId: Int) {
        showLoading.set(true)
        viewModelScope.launch {
            val result = repository.getAllPosts(userId)

            showLoading.set(false)
            when (result) {

                is AppResult.Success -> {
                    postList.value = result.successData
                    showError.value = null
                }
                is AppResult.Error -> {
                    showError.value = result.exception.message
                    try {
                        throw CustomException(result.exception.cause.toString(), result.message)
                    } catch (e: CustomException) {
                        FirebaseCrashlytics.getInstance().log(Log.DEBUG.toString()+ "Error : " +result.exception.cause.toString()+ "Message : " + result.message)
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
            }
        }
    }
}