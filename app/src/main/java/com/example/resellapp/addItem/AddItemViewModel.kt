package com.example.resellapp.addItem

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.google.firebase.database.FirebaseDatabase

class AddItemViewModel: ViewModel() {

    val database = FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app")

    val dbRef = database.getReference("Items")

    val _navToMyItems = MutableLiveData<Boolean?>()
    val navToMyItems: LiveData<Boolean?>
        get() = _navToMyItems

//    val _navAfterInsert = MutableLiveData<Boolean?>()
//    val navAfterInsert: LiveData<Boolean?>
//        get() = _navAfterInsert

    override fun onCleared() {
        super.onCleared()
    }

    init{

    }

    fun navigateToMyItems(){
        _navToMyItems.value = true
    }

    fun doneNavigating(){
        _navToMyItems.value = false
    }


//    fun afterInsert(name: String, price: Float, description: String){
//        if(insertItem(name, price, description))
//        {
//            _navAfterInsert.value = true
//        }
//    }
//
//
//    fun insertItem(name: String, price: Float, description: String): Boolean
//    {
//
//        if(name == "")
//        {
////            binding.name.error = "This field is required"
//            return false
//        }
//
//        if(price.toString() == "")
//        {
////            binding.price.error = "This field is required"
//            return false
//        }
//
//        if(description == "")
//        {
////            binding.name.error = "This field is required"
//            return false
//        }
//
//        val itemId = dbRef.push().key!!
//
//        val item = Item(itemId,name,price,description)
//
//        dbRef.child(itemId).setValue(item).addOnCompleteListener{
////            Toast.makeText(requireContext(),"Data inserted Success", Toast.LENGTH_LONG).show()
//        }.addOnFailureListener {
////            Toast.makeText(requireContext(),"Error ${it.message}", Toast.LENGTH_LONG).show()
//
//        }
//
//        Log.e("finished","yes")
//
//        return true
//    }





}