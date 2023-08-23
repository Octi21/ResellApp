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

    private val _imageFBlocation = MutableLiveData<List<String>>()
    val imageFBlocation: LiveData<List<String>>
        get() = _imageFBlocation

    private val _category = MutableLiveData<Int>()
    val category: LiveData<Int>
        get() = _category

    private val _subcategory = MutableLiveData<String>()
    val subcategory: LiveData<String>
        get() = _subcategory

    private val _size = MutableLiveData<String>()
    val size: LiveData<String>
        get() = _size

    override fun onCleared() {
        super.onCleared()
    }

    init{
        _imageUriList.value = emptyList()
        _category.value = 0
        _subcategory.value = ""
        _size.value = ""
        _imageFBlocation.value = emptyList()

    }

    fun navigateToMyItems(){
        _navToMyItems.value = true
    }

    fun doneNavigating(){
        _navToMyItems.value = false
    }

    fun setCategory(value:Int){
        _category.value = value
    }

    fun setSubcategory(value:String){
        _subcategory.value = value
    }

    fun setSize(size:String){
        _size.value = size
    }
    fun getSize():String{
        return _size.value.toString()
    }

    fun getSubcatogory():String{
        return _subcategory.value.toString()
    }

    fun getCatogory():String?{
        return when(_category.value){
            1 -> "Clothing"
            2 -> "Footwear"
            3 -> "Accessories"
            else -> null

        }

    }


    fun getImageList(): LiveData<List<Uri>>{
        return imageUriList
    }


    fun addImageUri(imageUri: Uri) {
        val currentList = _imageUriList.value?.toMutableList() ?: mutableListOf()
        currentList.add(imageUri)
        _imageUriList.value = currentList
    }

    fun addImageFBloc(image: String)
    {
        val currentList = _imageFBlocation.value?.toMutableList() ?: mutableListOf()
        currentList.add(image)
        _imageFBlocation.value = currentList
    }







}