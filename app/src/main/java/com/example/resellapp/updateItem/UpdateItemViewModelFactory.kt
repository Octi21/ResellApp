package com.example.resellapp.updateItem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.resellapp.addItem.AddItemViewModel

class UpdateItemViewModelFactory(
    private val itemId: String
): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateItemViewModel::class.java)) {
            return UpdateItemViewModel(itemId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}