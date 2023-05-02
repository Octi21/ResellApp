package com.example.resellapp.myItems

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.example.resellapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class MyItemsViewModel: ViewModel() {


    private var database: FirebaseDatabase =  FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

    private var dbRef: DatabaseReference = database.getReference("Items")



    private  var userId = Firebase.auth.currentUser!!.uid

    private var userRef: DatabaseReference = database.getReference("Users").child(userId)



    private val _itemsList = MutableLiveData<List<Item>>()
    val itemList: LiveData<List<Item>>
        get() = _itemsList

    private val _boughtItemsList = MutableLiveData<List<Item>>()
    val boughtItemList: LiveData<List<Item>>
        get() = _boughtItemsList


    private val _navigateToAddItem = MutableLiveData<Boolean?>()
    val navigateToAddItem: LiveData<Boolean?>
        get() = _navigateToAddItem

    private val _navigateToItemDetail = MutableLiveData<String?>()
    val navigateToItemDetail: LiveData<String?>
        get() = _navigateToItemDetail


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
                        Log.e("item","${item!!.userId}")
                        Log.e("item","${userId}")

                        if(userId!!.equals(item!!.userId) )
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


        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                if (user!!.boughtItems != null) {
                    _boughtItemsList.value = user!!.boughtItems?.map {
                        it
                    }
                }

                Log.e("boughtItems","${_boughtItemsList.value}")

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("boughtItemsError","${error}")
            }
        })
    }

    fun getItemsList(): LiveData<List<Item>>{
        return itemList
    }

    fun getBoughtItemsList(): LiveData<List<Item>>{
        return boughtItemList
    }

    fun onClickAddItem()
    {
        _navigateToAddItem.value = true
    }

    fun doneNavigating(){
        _navigateToAddItem.value = false
    }



    fun clickOnItem(id: String?){
        _navigateToItemDetail.value = id

    }

    fun doneNavigatingDetails(){
        _navigateToItemDetail.value = null
    }



}