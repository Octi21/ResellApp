package com.example.resellapp.shoppingCart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.example.resellapp.User
import com.example.resellapp.notification.NotificationData
import com.example.resellapp.notification.PushNotification
import com.example.resellapp.notification.RetrofitInstance
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

                if(user?.items != null)
                {
                    user?.items?.forEach {

                        // pushnotification to the items user
                        val notifIdRef = database.getReference("Users").child(it.userId!!).child("notificationId")
                        notifIdRef.addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val notificationId = snapshot.getValue(String::class.java)
                                Log.e("notificationId",notificationId.toString())
                                if(notificationId != null) {

                                    PushNotification(
                                        NotificationData("Sold item", "Someone Bought ${it.name}"),
                                        notificationId
                                    ).also {
                                        sendNotification(it)
                                    }
                                }

                            }


                            override fun onCancelled(error: DatabaseError) {
                                Log.e("notificationError", error.toString())
                            }
                        })



                        it.bought = true
                        idList.add(it.id!!)
                        user?.boughtItems = user?.boughtItems?.plus(it)
                        Log.e("boughtIthem","${user?.boughtItems}")
                    }
                }

                Log.e("boughtIthem","${user?.boughtItems}")


                user?.items = emptyList()

                for(id in idList)
                {
                    Log.e("id","${id}")
                    itemsRef.child(id).child("bought").setValue(true)
                }


                userRef.setValue(user)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("cartdelete","${error}")
            }
        })
    }
    fun sendNotifTo() {
        PushNotification(
            NotificationData("Sold item", "Someone Bought your item"),
            "ftocnU-YR3eg1oc7WvWL8w:APA91bEgvfkSKx0tkier2g6iBk1jzlKQJatFwgb63tWXomrsABkODT8yAjfXlp_WVHouEj4YuXGAULuwVH00R0QLt1syHqqCWiga9cKpvvA4Tdazwx3r-xNQFyQCDk55sxROyA3hySSg"
        ).also {
            sendNotification(it)
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.e("notif", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("notif2", response.code().toString())
            }
        } catch(e: Exception) {
            Log.e("notif3", e.toString())
        }
    }
}