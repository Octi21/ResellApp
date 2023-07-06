package com.example.resellapp.addItem

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.google.firebase.database.FirebaseDatabase

class AddItemViewModel: ViewModel() {

    val database = FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app")

    val dbRef = database.getReference("Items")

    val _navToMyItems = MutableLiveData<Boolean?>()
    val navToMyItems: LiveData<Boolean?>
        get() = _navToMyItems

    private val _imageUriList = MutableLiveData<List<Uri>>()
    val imageUriList: LiveData<List<Uri>>
        get() = _imageUriList

    override fun onCleared() {
        super.onCleared()
    }

    init{
        _imageUriList.value = emptyList()
    }

    fun navigateToMyItems(){
        _navToMyItems.value = true
    }

    fun doneNavigating(){
        _navToMyItems.value = false
    }



    fun getImageList(): LiveData<List<Uri>>{
        return imageUriList
    }

    fun addImageUri(imageUri: Uri) {
        val currentList = _imageUriList.value?.toMutableList() ?: mutableListOf()
        currentList.add(imageUri)
        _imageUriList.value = currentList
    }






}