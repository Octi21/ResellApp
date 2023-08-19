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

    private var deletedRef: DatabaseReference = database.getReference("Deleted")
    private var itemsRef: DatabaseReference = database.getReference("Items")
    private var userRef: DatabaseReference = database.getReference("Users").child(userId)

    private val _navigateToItemDetail = MutableLiveData<String?>()
    val navigateToItemDetail: LiveData<String?>
        get() = _navigateToItemDetail


    private val _deletedList = MutableLiveData<List<String>>()
    val deletedList: LiveData<List<String>>
        get() = _deletedList

    private val _likeditemsList = MutableLiveData<List<Item>>()
    val likeditemsList: LiveData<List<Item>>
        get() = _likeditemsList

    var newList: MutableList<Item> = mutableListOf()


    init {
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

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                //!!!if item if bought!!!   or if item is deleted!!!
                val list = user!!.likedItems ?: emptyList()
                newList = mutableListOf()

                val taskList = mutableListOf<Task<DataSnapshot>>()
//                val resultFileDataList = List<DataSnapshot>()

                val databaseReferenceTask: Task<DataSnapshot> = deletedRef.get()
                taskList.add(databaseReferenceTask)
                list.forEach {
                    val databaseReferenceTask: Task<DataSnapshot> = itemsRef.child(it.id!!).get()
                    taskList.add(databaseReferenceTask)


                }

                val resultTask = Tasks.whenAll(taskList)
                resultTask.addOnCompleteListener {
                    var aux =1
                    val deleted = mutableListOf<String>()
                    for (task in taskList) {
                        if(aux ==1)
                        {
                            val result: DataSnapshot = task.result
                            for(elem in result.children)
                            {
                                Log.e("elem",elem.value.toString())
                                deleted.add(elem.value.toString())
                            }

                            Log.e("!!!!$aux",result.toString())

                        }
                        if(aux!=1)
                        {
                            val item = task.result.getValue(Item::class.java)
                            Log.e("!!!!$aux",item.toString())
                            val test = deleted.all{ it != item!!.id}
                            if(item!!.bought !=true and test)
                                newList.add(item!!)
                        }

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


    fun deleteIfInList(list:List<String>){
        val l:MutableList<Item> = _likeditemsList.value?.toMutableList() ?: mutableListOf()
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
        _likeditemsList.value = newList.toList()
    }




    fun clickOnItem(id: String?){
        _navigateToItemDetail.value = id
    }
    fun doneNavigatingDetails(){
        _navigateToItemDetail.value = null
    }



}