package com.example.resellapp.myItems

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.google.firebase.database.*

class MyItemsViewModel: ViewModel() {


    private var database: FirebaseDatabase =  FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

    private var dbRef: DatabaseReference = database.getReference("Items")


    private val _itemsList = MutableLiveData<List<Item>>()
    val itemList: LiveData<List<Item>>
        get() = _itemsList


    private val _navigateToAddItem = MutableLiveData<Boolean?>()
    val navigateToAddItem: LiveData<Boolean?>
        get() = _navigateToAddItem


    override fun onCleared() {
        super.onCleared()
    }

    init {
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val items: MutableList<Item> = mutableListOf()
                    for(json in snapshot.children)
                    {
                        val item = json.getValue(Item::class.java)

                        Log.e("item","${item}")
                        item?.let{
                            items.add(it)
                        }

                    }
                    _itemsList.value = items


                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ItemViewModel", "loadItems:onCancelled", error.toException())
            }
        })
    }

    fun onClickAddItem()
    {
        _navigateToAddItem.value = true
    }

    fun doneNavigating(){
        _navigateToAddItem.value = false
    }

    fun getItemsList(): LiveData<List<Item>>{
        return itemList
    }




//    fun getItemsFromDatabase() {
//        // Call the modified version of the getYourItems() function
//        getYourItems { items ->
//            // Set the value of the MutableLiveData variable to the retrieved items
//            this.items = items
//
//        }
//    }
//
//    fun getYourItems(callback: (MutableList<Item?>) -> Unit) {
//        val list: MutableList<Item?> = mutableListOf()
//
//        dbRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()) {
//                    for(json in snapshot.children) {
//                        val data = json.getValue(Item::class.java)
//                        list.add(data)
//                        Log.e("data", "${data}")
//                    }
//                }
//                callback(list)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//    }


}