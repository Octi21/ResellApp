package com.example.resellapp.itemDetailHome

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.example.resellapp.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ItemDetailHomeViewModel(
    private val itemId: String = ""):  ViewModel(){

    private var database: FirebaseDatabase =  FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

    private var dbRef: DatabaseReference = database.getReference("Items")

    private val userId = Firebase.auth.currentUser!!.uid


    private val _item = MutableLiveData<Item?>()
    val item: LiveData<Item?>
        get() = _item

    private val _navToHome = MutableLiveData<Boolean?>()
    val navToHome: LiveData<Boolean?>
        get() = _navToHome

    private val _showToast = MutableLiveData<Boolean?>()
    val showToast: LiveData<Boolean?>
        get() = _showToast



    init{
        dbRef.child(itemId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _item.value = snapshot.getValue(Item::class.java)
            }


            override fun onCancelled(error: DatabaseError) {
                Log.e("showItem", "loadItems:onCancelled", error.toException())
            }
        })
    }

    fun navToHome(){
        _navToHome.value = true
    }

    fun doneNavigating(){
        _navToHome.value = null
    }

    fun addItemToYourCart(){
        val userRef = database.getReference("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>(User::class.java)
                val items = user?.items?.toMutableList() ?: mutableListOf()

                _item.value?.let {
                    var ok = 1
                    for(elem in items)
                    {
                        if(elem.id.equals(_item.value!!.id))
                        {
                            ok =0
                        }
                    }
                    if(ok ==1)
                    {
                        items.add(it)
                    }
                    else
                    {
                        _showToast.value = true
                    }
                }

                user?.items = items

                userRef.setValue(user)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("addToShoppingCartError","${error}")
            }
        })
    }

    fun doneToast(){
        _showToast.value = false
    }


}