package com.example.resellapp.itemDetailHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.resellapp.itemDetail.ItemDetailViewModel

class ItemDetailHomeViewModelFactory (
    private val itemId: String
) : ViewModelProvider.Factory{

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemDetailHomeViewModel::class.java)) {
            return ItemDetailHomeViewModel(itemId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}