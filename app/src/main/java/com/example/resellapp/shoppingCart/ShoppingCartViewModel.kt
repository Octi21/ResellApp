package com.example.resellapp.shoppingCart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.example.resellapp.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ShoppingCartViewModel: ViewModel() {

    private var database: FirebaseDatabase =  FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

    private  var userId = Firebase.auth.currentUser!!.uid

    private val _itemsList = MutableLiveData<List<Item>>()
    val itemList: LiveData<List<Item>>
        get() = _itemsList

    init{
        val userRef = database.getReference("Users").child(userId)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)


                if (user!!.items != null) {
                    _itemsList.value = user!!.items?.map {
                        it
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("cartError","${error}")
            }
        })
    }

    fun getItemsList(): LiveData<List<Item>>{
        return itemList
    }


    fun deleteItemFromCart(id: String?){
        val userRef = database.getReference("Users").child(userId)

        userRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>(User::class.java)
                val items = user?.items?.toMutableList() ?: mutableListOf()

                items.removeAll{
                    it.id == id
                }
                Log.e("list","${items}")

                user?.items = items.toList()

//                Log.e("newUser","${user}")

                userRef.setValue(user)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("cartdelete","${error}")
            }
        })
    }

    fun buyItemFromCart(){
        val userRef = database.getReference("Users").child(userId)

        userRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>(User::class.java)


                user?.items = emptyList()

//                Log.e("newUser","${user}")

                userRef.setValue(user)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("cartdelete","${error}")
            }
        })
    }
}