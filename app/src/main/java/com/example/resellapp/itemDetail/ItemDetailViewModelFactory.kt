package com.example.resellapp.itemDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ItemDetailViewModelFactory(
    private val itemId: String
) : ViewModelProvider.Factory{

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemDetailViewModel::class.java)) {
            return ItemDetailViewModel(itemId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}