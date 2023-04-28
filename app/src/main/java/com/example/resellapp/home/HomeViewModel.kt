package com.example.resellapp.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class HomeViewModel: ViewModel() {

    private var database: FirebaseDatabase =  FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

    private var dbRef: DatabaseReference = database.getReference("Items")

    private  var userId = Firebase.auth.currentUser!!.uid


    private val _navigateToItemDetail = MutableLiveData<String?>()
    val navigateToItemDetail: LiveData<String?>
        get() = _navigateToItemDetail

    private val _itemsList = MutableLiveData<List<Item>>()
    val itemList: LiveData<List<Item>>
        get() = _itemsList


    init{
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val items: MutableList<Item> = mutableListOf()
                    for(json in snapshot.children)
                    {
                        val item = json.getValue(Item::class.java)
                        Log.e("item","${item!!.userId}")
                        Log.e("item","${userId}")

                        if(!userId!!.equals(item!!.userId) )
                        {
                            Log.e("item","${item}")
                            item?.let{
                                items.add(it)
                            }
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

    fun getItemsList(): LiveData<List<Item>>{
        return itemList
    }


    fun clickOnItem(id: String?){
        _navigateToItemDetail.value = id

    }

    fun doneNavigatingDetails(){
        _navigateToItemDetail.value = null
    }


}