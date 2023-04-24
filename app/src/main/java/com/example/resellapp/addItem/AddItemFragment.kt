package com.example.resellapp.addItem

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.resellapp.Item
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentAddItemBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class AddItemFragment: Fragment() {

    private lateinit var binding: FragmentAddItemBinding

    private lateinit var database: FirebaseDatabase

    private lateinit var dbRef: DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_add_item,container,false)


        val viewModelFactory = AddItemsViewModelFactory()

        val addItemViewModel = ViewModelProvider(this,viewModelFactory).get(AddItemViewModel::class.java)

        binding.addItemViewModel = addItemViewModel

        addItemViewModel.navToMyItems.observe(viewLifecycleOwner, Observer {
            if(it == true)
            {
                val action = AddItemFragmentDirections.actionAddItemFragmentToMyItemsFragment()
                findNavController().navigate(action)

                addItemViewModel.doneNavigating()
            }
        })



        // onClick on xml file


        database = FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app")

        dbRef = database.getReference("Items")





        binding.confirmButton.setOnClickListener{
            if(addObject()) {

                val action = AddItemFragmentDirections.actionAddItemFragmentToMyItemsFragment()
                findNavController().navigate(action)
            }
            else{
                Log.e("nuseajunge","nuseajunge")
            }
        }

        return binding.root
    }


    private fun addObject(): Boolean{

        val name = binding.nameText.text.toString()
        val price = binding.priceText.text.toString()
        val description = binding.descriptionText.text.toString()


        if(name == "")
        {
            binding.name.error = "This field is required"


            return false
        }
        binding.name.error = null


        val price2 = try {
            price.toFloat()
        } catch (e: NumberFormatException) {
            binding.price.error = "Please enter a valid price"
            return false
        }
        binding.price.error = null



        if(description == "")
        {
            binding.description.error = "This field is required"
            return false
        }
        binding.description.error = null


        val itemId = dbRef.push().key!!

        val item = Item(itemId,name,price2,description)

        dbRef.child(itemId).setValue(item).addOnCompleteListener{
                Toast.makeText(requireContext(),"Data inserted Success", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Error ${it.message}", Toast.LENGTH_LONG).show()

        }

        Log.e("finished","yes")

        return true

    }
}
