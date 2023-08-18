package com.example.resellapp.itemDetailHome

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.example.resellapp.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ItemDetailHomeViewModel(
    private val itemId: String = ""
):  ViewModel(){

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

    private val _addToast = MutableLiveData<Boolean?>()
    val addTast: LiveData<Boolean?>
        get() = _addToast

    private val _liked = MutableLiveData<Boolean?>()
    val liked: LiveData<Boolean?>
        get() = _liked




    init{
        dbRef.child(itemId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _item.value = snapshot.getValue(Item::class.java)
            }


            override fun onCancelled(error: DatabaseError) {
                Log.e("showItem", "loadItems:onCancelled", error.toException())
            }
        })

        val userRef = database.getReference("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                val likedList = user!!.likedItems?.toMutableList() ?: mutableListOf()
                for(elem in likedList)
                {
                    if(elem.id == itemId) {
                        _liked.value = true
                        break
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("addTofav","${error}")
            }
        })

    }

    fun navToHome(){
        _navToHome.value = true
    }

    fun doneNavigating(){
        _navToHome.value = null
    }

    fun addItemToLiked(){
        val userRef = database.getReference("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                val likedList = user!!.likedItems?.toMutableList() ?: mutableListOf()
                _item.value?.let {
                    var ok = true
                    for(elem in likedList)
                    {
                        if(elem.id == itemId) {
                            likedList.remove(elem)
                            ok = false
                            _liked.value = false
                            break
                        }
                    }
                    if(ok)
                    {
                        likedList.add(it)
                        _liked.value = true
                    }
                }
                user?.likedItems = likedList
                userRef.setValue(user)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("addTofav","${error}")
            }
        })
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
                        if(_item.value != null && elem.id.equals(_item.value!!.id))
                        {
                            ok =0
                        }
                    }
                    if(ok ==1)
                    {
                        _addToast.value = true
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
        _addToast.value = false
    }



}