package com.example.mvvmexample.views.todos.interfaces

import com.example.mvvmexample.model.todos.TodosData

interface TodosClickListener {
    fun onItemClick(data : TodosData)
}