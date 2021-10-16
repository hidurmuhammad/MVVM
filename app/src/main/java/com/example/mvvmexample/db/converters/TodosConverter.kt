package com.example.mvvmexample.db.converters

import androidx.room.TypeConverter
import com.example.mvvmexample.model.todos.TodosData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TodosConverter {
    @TypeConverter
    fun fromStringTodo(value: String): List<TodosData> {
        val listType = object : TypeToken<List<TodosData>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayListToStringTodo(list: List<TodosData>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}