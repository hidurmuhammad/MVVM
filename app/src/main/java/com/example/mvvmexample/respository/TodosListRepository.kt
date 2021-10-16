package com.example.mvvmexample.respository

import com.example.mvvmexample.model.todos.TodosData
import com.example.mvvmexample.utils.AppResult

interface TodosListRepository {
    suspend fun getAllTodos(): AppResult<List<TodosData>>

}