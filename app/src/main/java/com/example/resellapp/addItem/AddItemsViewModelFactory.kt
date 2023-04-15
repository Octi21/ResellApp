package com.example.resellapp.addItem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddItemsViewModelFactory: ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddItemViewModel::class.java)) {
            return AddItemViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}