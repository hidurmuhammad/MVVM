package com.example.mvvmexample.viewmodel

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmexample.model.PhotoData
import com.example.mvvmexample.respository.PhotosListRepository
import com.example.mvvmexample.utils.AppResult
import com.example.mvvmexample.utils.CustomApiException
import com.example.mvvmexample.utils.CustomException
import com.example.mvvmexample.utils.SingleLiveEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch

class PhotoListViewModel (private val repository : PhotosListRepository) : ViewModel() {

    val showLoading = ObservableBoolean()
    val photosList = MutableLiveData<List<PhotoData>?>()
    val showError = SingleLiveEvent<String?>()

    fun getAllPhotos() {
        showLoading.set(true)
        viewModelScope.launch {
            val result = repository.getAllPhotos()

            showLoading.set(false)
            when (result) {
                is AppResult.Success -> {
                    photosList.value = result.successData
                    showError.value = null
                }

                is AppResult.Error -> {
                    showError.value = result.exception.message
                    try {
                        Log.d("error12:",result.exception.cause.toString()+ result.message)
                        throw CustomException(result.exception.cause.toString(), result.message)
                    } catch (e: CustomException) {
                        FirebaseCrashlytics.getInstance().log(Log.DEBUG.toString()+ "Error : " +result.exception.toString()+ "Message : " + result.message)
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }

                }
            }

        }
    }
}