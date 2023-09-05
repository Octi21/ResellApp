package com.example.resellapp.itemDetail


import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resellapp.Item
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemDetailViewModel(
    private val itemId: String = ""):  ViewModel(){



    private var database: FirebaseDatabase =  FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

    private var dbRef: DatabaseReference = database.getReference("Items")

    private var deleteRef: DatabaseReference = database.getReference("Deleted")

    val storage = FirebaseStorage.getInstance("gs://androidkotlinresellapp.appspot.com").getReference("Images")




    private val _item = MutableLiveData<Item?>()
    val item: LiveData<Item?>
        get() = _item

    private val _navToMyItems = MutableLiveData<Boolean?>()
    val navToMyItems: LiveData<Boolean?>
        get() = _navToMyItems

    var list = listOf<String>()




    init{
         dbRef.child(itemId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Item::class.java)
                _item.value = item
                list = item?.imageFirebaseLocations ?: listOf()
                Log.e("listEleme",list.toString())
            }


            override fun onCancelled(error: DatabaseError) {
                Log.e("showItem", "loadItems:onCancelled", error.toException())
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun navToMyItems(){
        _navToMyItems.value = true
    }



    fun doneNavigating(){
        _navToMyItems.value = null
    }

//    fun viewDeleteButton(): Int{
//        dbRef.child(itemId).child("bought").addListenerForSingleValueEvent(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val boughtValue = snapshot.value
//                if(boughtValue == true)
//                {
//                    return View.VISIBLE
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//        Log.e("true","${}")
//        if(_item.value?.bought == true)
//        {
////            Log.e("true","${_item.value?.bought}")
//            return View.VISIBLE
//        }
////        Log.e("false","${_item.value?.bought}")
//        return View.GONE
//    }


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

    fun deleteItem() {
        dbRef.child(itemId).removeValue()
    }

    fun addDeletedItemToDatabase()
    {
        deleteRef.child(itemId).setValue(itemId)
    }





}