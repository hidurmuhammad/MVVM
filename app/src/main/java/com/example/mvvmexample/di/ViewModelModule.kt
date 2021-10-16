package com.example.mvvmexample.di

import com.example.mvvmexample.viewmodel.PhotoListViewModel
import com.example.mvvmexample.viewmodel.PostListViewModel
import com.example.mvvmexample.viewmodel.TodosListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule:Module = module {
    // Specific viewModel pattern to tell Koin how to build ViewModel
    viewModel {
        PhotoListViewModel(repository = get())

    }
    viewModel {
        TodosListViewModel(repository = get())
    }

    viewModel {
        PostListViewModel(repository = get())
    }

}