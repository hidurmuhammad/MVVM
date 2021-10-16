package com.example.mvvmexample.viewmodel

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmexample.model.todos.TodosData
import com.example.mvvmexample.respository.TodosListRepository
import com.example.mvvmexample.utils.AppResult
import com.example.mvvmexample.utils.CustomException
import com.example.mvvmexample.utils.SingleLiveEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch

class TodosListViewModel(private val repository: TodosListRepository) : ViewModel() {

    val showLoading = ObservableBoolean()
    val todosList = MutableLiveData<List<TodosData>?>()
    val showError = SingleLiveEvent<String?>()

    fun getAllTodos() {
        showLoading.set(true)
        viewModelScope.launch {
            val result = repository.getAllTodos()

            showLoading.set(false)
            when (result) {
                is AppResult.Success -> {
                    todosList.value = result.successData
                    showError.value = null
                }
                is AppResult.Error ->{
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