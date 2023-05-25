package com.example.resellapp.itemDetail


import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.google.firebase.database.*

class ItemDetailViewModel(
    private val itemId: String = ""):  ViewModel(){



    private var database: FirebaseDatabase =  FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

    private var dbRef: DatabaseReference = database.getReference("Items")



    private val _item = MutableLiveData<Item?>()
    val item: LiveData<Item?>
        get() = _item

    private val _navToMyItems = MutableLiveData<Boolean?>()
    val navToMyItems: LiveData<Boolean?>
        get() = _navToMyItems




    init{
         dbRef.child(itemId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                _item.value = snapshot.getValue(Item::class.java)
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

    fun deleteItem() {
        dbRef.child(itemId).removeValue()
        _navToMyItems.value = true
    }




}