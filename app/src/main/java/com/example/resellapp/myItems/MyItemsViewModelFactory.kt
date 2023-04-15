package com.example.resellapp.myItems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyItemsViewModelFactory : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyItemsViewModel::class.java)) {
            return MyItemsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}