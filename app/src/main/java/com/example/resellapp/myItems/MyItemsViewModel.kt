package com.example.resellapp.myItems

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyItemsViewModel: ViewModel() {


    private val _navigateToAddItem = MutableLiveData<Boolean?>()
    val navigateToAddItem: LiveData<Boolean?>
        get() = _navigateToAddItem


    override fun onCleared() {
        super.onCleared()
    }

    init {

    }

    fun onClickAddItem()
    {
        _navigateToAddItem.value = true
    }

    fun doneNavigating(){
        _navigateToAddItem.value = false
    }


}