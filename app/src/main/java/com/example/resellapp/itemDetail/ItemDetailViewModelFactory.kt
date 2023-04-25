package com.example.resellapp.itemDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ItemDetailViewModelFactory: ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemDetailViewModel::class.java)) {
            return ItemDetailViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}