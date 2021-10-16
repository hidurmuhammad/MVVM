package com.example.mvvmexample.respository

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.mvvmexample.api.ApiService
import com.example.mvvmexample.db.dao.TodosDao
import com.example.mvvmexample.model.todos.TodosData
import com.example.mvvmexample.utils.AppResult
import com.example.mvvmexample.utils.NetworkManager
import com.example.mvvmexample.utils.Utils
import com.example.mvvmexample.utils.noNetworkConnectivityError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoListRepositoryImpl(
    private val api: ApiService,
    private val context: Context,
    private val dao: TodosDao
) : TodosListRepository {

    override suspend fun getAllTodos(): AppResult<List<TodosData>> {
        if (NetworkManager.isOnline(context)) {
            return try {
                val response = api.getAllTodos()
                if (response.isSuccessful) {
                    //save the data
                    response.body()?.let {
                        withContext(Dispatchers.IO) { dao.add(it) }
                    }
                    Utils.handleSuccess(response)
                } else {
                    Utils.handleApiError(response)
                }
            } catch (e: Exception) {
                AppResult.Error(e)
            }
        } else {
            //check in db if the data exists
            val data = geTodosDataFromCache()
            return if (data.isNotEmpty()) {
                Log.d(ContentValues.TAG, "from db")
                AppResult.Success(data)
            } else
            //no network
                context.noNetworkConnectivityError()
        }
    }

    private suspend fun geTodosDataFromCache(): List<TodosData> {
        return withContext(Dispatchers.IO) {
            dao.findAllTodos()
        }
    }
}