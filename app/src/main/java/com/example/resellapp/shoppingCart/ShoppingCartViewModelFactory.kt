package com.example.resellapp.shoppingCart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.resellapp.myItems.MyItemsViewModel

class ShoppingCartViewModelFactory : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingCartViewModel::class.java)) {
            return ShoppingCartViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}