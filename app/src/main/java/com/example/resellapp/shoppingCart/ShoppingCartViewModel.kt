package com.example.resellapp.shoppingCart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.example.resellapp.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ShoppingCartViewModel: ViewModel() {

    private var database: FirebaseDatabase =  FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

    private  var userId = Firebase.auth.currentUser!!.uid

    private var itemsRef: DatabaseReference = database.getReference("Items")


    private val _itemsList = MutableLiveData<List<Item>>()
    val itemList: LiveData<List<Item>>
        get() = _itemsList

    init{
        val userRef = database.getReference("Users").child(userId)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)


                if (user!!.items != null) {
                    _itemsList.value = user!!.items?.filter {
                        it.bought != true
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

                val idList: MutableList<String> = mutableListOf()

                user?.items?.forEach {
                    it.bought = true
                    idList.add(it.id!!)
                    user?.boughtItems = user?.boughtItems?.plus(it)
                    Log.e("boughtIthem","${user?.boughtItems}")
                }
                Log.e("boughtIthem","${user?.boughtItems}")


                user?.items = emptyList()

                for(id in idList)
                {
                    Log.e("id","${id}")
                    itemsRef.child(id).child("bought").setValue(true)
                }

                //                itemsRef.addListenerForSingleValueEvent(object :ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        Log.e("errorMakeBought","${error}")
//                    }
//                })



//                Log.e("newUser","${user}")

                userRef.setValue(user)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("cartdelete","${error}")
            }
        })
    }
}