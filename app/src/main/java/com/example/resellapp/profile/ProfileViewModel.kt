package com.example.resellapp.profile

import android.util.Log
import androidx.lifecycle.*
import com.example.resellapp.Item
import com.example.resellapp.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class ProfileViewModel: ViewModel() {

    private var database: FirebaseDatabase =  FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

    private  var userId = Firebase.auth.currentUser!!.uid

    private var itemsRef: DatabaseReference = database.getReference("Items")
    private var userRef: DatabaseReference = database.getReference("Users").child(userId)

    private val _navigateToItemDetail = MutableLiveData<String?>()
    val navigateToItemDetail: LiveData<String?>
        get() = _navigateToItemDetail


    private val _likeditemsList = MutableLiveData<List<Item>>()
    val likeditemsList: LiveData<List<Item>>
        get() = _likeditemsList

    var newList: MutableList<Item> = mutableListOf()


    init {

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                //!!!if item if bought!!!   or if item is deleted!!!
                val list = user!!.likedItems ?: emptyList()
                newList = mutableListOf()

                val taskList = mutableListOf<Task<DataSnapshot>>()
//                val resultFileDataList = List<DataSnapshot>()

                list.forEach {
                    val databaseReferenceTask: Task<DataSnapshot> = itemsRef.child(it.id!!).get()
                    taskList.add(databaseReferenceTask)


                }

                val resultTask = Tasks.whenAll(taskList)
                resultTask.addOnCompleteListener {
                    var aux =1
                    for (task in taskList) {
                        val item = task.result.getValue(Item::class.java)
                        Log.e("!!!!$aux",item.toString())
                        if(item!!.bought !=true)
                            newList.add(item!!)
                        aux +=1
                    }
                    Log.e("!!!!!!!!!!!!!!", newList.toString())

                    user!!.likedItems = newList
                    userRef.setValue(user)

                    _likeditemsList.value = newList ?: listOf()

                }

//                _likeditemsList.value = user!!.likedItems ?: listOf()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("errorProfile", error.toString())
            }
        })

    }





    fun clickOnItem(id: String?){
        _navigateToItemDetail.value = id
    }
    fun doneNavigatingDetails(){
        _navigateToItemDetail.value = null
    }



}