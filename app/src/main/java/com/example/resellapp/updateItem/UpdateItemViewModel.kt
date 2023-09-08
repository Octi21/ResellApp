package com.example.resellapp.updateItem

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resellapp.Item
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class UpdateItemViewModel(
    private val itemId: String = ""
): ViewModel() {

    private var database: FirebaseDatabase =  FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

    private var dbRef: DatabaseReference = database.getReference("Items")


    val storage = FirebaseStorage.getInstance("gs://androidkotlinresellapp.appspot.com").getReference("Images")


    var list = mutableListOf<String>()


    private val _item = MutableLiveData<Item?>()
    val item: LiveData<Item?>
        get() = _item

    private val _navToItemDetail = MutableLiveData<Boolean?>()
    val navToItemDetail: LiveData<Boolean?>
        get() = _navToItemDetail

    private val _imageUriList = MutableLiveData<List<Uri>>()
    val imageUriList: LiveData<List<Uri>>
        get() = _imageUriList

    private val _imageStorageList = MutableLiveData<List<String>>()
    val imageStorageList: LiveData<List<String>>
        get() = _imageStorageList

    private val _imageLocationList = MutableLiveData<List<String>>()
    val imageLocationList: LiveData<List<String>>
        get() = _imageLocationList




    init {
        _imageUriList.value = emptyList()
        _imageStorageList.value = emptyList()

        dbRef.child(itemId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Item::class.java)
                _item.value = item
//                list = item?.imageFirebaseLocations ?: listOf()
                _imageLocationList.value =  item?.imageFirebaseLocations ?: emptyList()
                _imageStorageList.value = item?.imageUrlList ?: emptyList()
//                Log.e("listEleme",list.toString())
            }


            override fun onCancelled(error: DatabaseError) {
                Log.e("showItem", "loadItems:onCancelled", error.toException())
            }
        })
    }

    fun getItem(): Item{
        return _item.value!!
    }

    fun navToItemDetail(){
        _navToItemDetail.value = true
    }

    fun deneNavigating(){
        _navToItemDetail.value = false
    }

    fun getImageList(): LiveData<List<Uri>>{
        return imageUriList
    }

    fun getAfterDelete(): List<String>{
        return imageStorageList.value!!
    }

    fun getAfterDeleteLocation(): List<String>{
        return _imageLocationList.value!!
    }


    fun addImageUri(imageUri: Uri) {
        val currentList = _imageUriList.value?.toMutableList() ?: mutableListOf()
        currentList.add(imageUri)
        _imageUriList.value = currentList
    }

    fun deleteImageAtIndex(index: Int) {
        val currentList = _imageStorageList.value?.toMutableList() ?: mutableListOf()
        val currentList2 = _imageLocationList.value?.toMutableList() ?: mutableListOf()



        if (index in 0 until currentList.size) {
            list.add(currentList2[index])

            currentList.removeAt(index)
            currentList2.removeAt(index)

            _imageStorageList.value = currentList
            _imageLocationList.value = currentList2

        }
    }

    fun deleteImages(){
        viewModelScope.launch {
            try {
                for (imageLocation in list) {
                    // Create a Firebase Storage reference for each image
                    val imageRef = storage.child(imageLocation)

                    imageRef.delete()
                        .addOnSuccessListener {
                            Log.d("DeleteImage", "Image deleted: $imageLocation")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("DeleteImage", "Error deleting image $imageLocation: ${exception.message}")
                        }
                }
            } catch (e: Exception) {
                Log.e("DeleteImages", "Error deleting images: ${e.message}")
            }
        }

        Log.e("imageLocations",list.toString())
    }




}