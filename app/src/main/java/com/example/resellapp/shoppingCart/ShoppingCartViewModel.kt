package com.example.resellapp.shoppingCart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.example.resellapp.User
import com.example.resellapp.notification.FirebaseService
import com.example.resellapp.notification.NotificationData
import com.example.resellapp.notification.PushNotification
import com.example.resellapp.notification.RetrofitInstance
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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

    private var deletedRef: DatabaseReference = database.getReference("Deleted")
    private var itemsRef: DatabaseReference = database.getReference("Items")


    private val _itemsList = MutableLiveData<List<Item>>()
    val itemList: LiveData<List<Item>>
        get() = _itemsList

    private val _deletedList = MutableLiveData<List<String>>()
    val deletedList: LiveData<List<String>>
        get() = _deletedList

    private val _emptyListToast = MutableLiveData<Boolean?>()
    val emptyListToast: LiveData<Boolean?>
        get() = _emptyListToast

    init{
        deletedRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<String> = mutableListOf()
                for(elem in snapshot.children)
                {
//                    Log.e("!!delid",elem.key.toString())
                    list.add(elem.key.toString())
                }
                _deletedList.value = list.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("errorDel",error.toString())
            }
        })


        val taskList = mutableListOf<Task<DataSnapshot>>()

        var databaseReferenceTask: Task<DataSnapshot> = deletedRef.get()
        taskList.add(databaseReferenceTask)
        databaseReferenceTask = database.getReference("Users").child(userId).child("items").get()
        taskList.add(databaseReferenceTask)

        val resultTask = Tasks.whenAll(taskList)
        resultTask.addOnCompleteListener {
            var aux = 1
            val deleted = mutableListOf<String>()
            var newList: MutableList<Item> = mutableListOf()
            for (task in taskList) {
                if (aux == 1) {
                    val result: DataSnapshot = task.result
                    for (elem in result.children) {
                        Log.e("elem", elem.value.toString())
                        deleted.add(elem.value.toString())
                    }

                    Log.e("!!!!$aux", result.toString())
                }
                if (aux == 2) {
                    val result: DataSnapshot = task.result
                    for(elem in result.children)
                    {
                        val item = elem.getValue(Item::class.java)
                        Log.e("elem2", item.toString())
                        if(item != null && item.id !=null)
                        {
                            val test = deleted.all{ it != item!!.id}
                            if(item!!.bought !=true and test)
                                newList.add(item!!)
                        }

                    }
                    Log.e("!!!!$aux", result.toString())
                }

                aux += 1
            }
            Log.e("!!!!!!!!!!!!!!", newList.toString())

            database.getReference("Users").child(userId).child("items").setValue(newList)
        }


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

                val listsize = user?.items?.size ?: 0
                if(listsize !=0)
                {
                    Log.e("MyCartItems","${user?.items.toString()}")
                    user?.items?.forEach {

                        // pushnotification to the items user
                        val notifIdRef = database.getReference("Users").child(it.userId!!).child("notificationId")
                        notifIdRef.addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val notificationId = snapshot.getValue(String::class.java)
                                Log.e("notificationId",notificationId.toString())
                                if(notificationId != null) {
                                    FirebaseService.token = notificationId

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

                    Log.e("boughtIthem","${user?.boughtItems}")


                    user?.items = emptyList()

                    for(id in idList)
                    {
                        Log.e("id","${id}")
                        itemsRef.child(id).child("bought").setValue(true)
                    }


                    userRef.setValue(user)
                }
                else
                {
                    Log.e("emptyBag","EmptyBag")
                    _emptyListToast.value = true
                }



            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("cartdelete","${error}")
            }
        })
    }
    fun sendNotifTo() {
        PushNotification(
            NotificationData("Sold item", "Someone Bought your item"),
            "dwEEpQezTeesSLmu9oV7Ah:APA91bG785ltVpPjCy6J02mI7kgZmkB3kPvUa5KC1gPua8zvVEoRZukMuITgao-dVizgOt9VYvi1LIZIbUfG2uas6yGRl0FXrSWQ8xu8vY8TAXc8sM4eFMnktZDVb0kfSBm-jW6TWvG4"
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


    fun deleteIfInList(list:List<String>){
        val l:MutableList<Item> = _itemsList.value?.toMutableList() ?: mutableListOf()
        val newList: MutableList<Item> = mutableListOf()
        Log.e("merge",l.toString())
        l.forEach{
            var ok = true
            list.forEach{ it2 ->
                if(it.id == it2)
                {
                    ok = false
                }

            }
            if(ok)
            {
                newList.add(it)
            }

        }

        database.getReference("Users").child(userId).child("items").setValue(newList.toList())


        _itemsList.value = newList.toList()
    }



    fun resetToast(){
        _emptyListToast.value = false
    }

}