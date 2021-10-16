package com.example.mvvmexample.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvvmexample.model.PhotoData
import com.example.mvvmexample.model.todos.TodosData

@Dao
interface TodosDao {

    @Query("SELECT * FROM Todos")
    fun findAllTodos(): List<TodosData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(todos: List<TodosData>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun createTodosIfNotExists(todos: TodosData): Long

}