package com.example.resellapp.myItems

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.resellapp.Item
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentMyItemBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*
import java.util.*

class MyItemsFragment: Fragment() {


    private lateinit var binding: FragmentMyItemBinding


    private lateinit var database: FirebaseDatabase

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the user's locale
        FirebaseAnalytics.getInstance(requireContext()).setUserProperty("locale", Locale.getDefault().language)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_my_item,container,false)

        val viewModelFactory = MyItemsViewModelFactory()

        val myItemsViewModel = ViewModelProvider(this,viewModelFactory).get(MyItemsViewModel::class.java)

        binding.myItemsViewModel = myItemsViewModel

        myItemsViewModel.navigateToAddItem.observe(viewLifecycleOwner, Observer {
            if(it == true)
            {
                val action = MyItemsFragmentDirections.actionMyItemsFragmentToAddItemFragment()
                findNavController().navigate(action)

                myItemsViewModel.doneNavigating()
            }
        })

        // onClock on xml file




        //nav to detail onClick
        myItemsViewModel.navigateToItemDetail.observe(viewLifecycleOwner, Observer {
            if (it != null)
            {
                val action = MyItemsFragmentDirections.actionMyItemsFragmentToItemDetailFragment(it)
                findNavController().navigate(action)

                myItemsViewModel.doneNavigatingDetails()
            }
        })


        /// adapater things

        val adapter = ItemsAdapter(ItemListener { itemId ->
            Log.e("itemId","${itemId}")
            myItemsViewModel.clickOnItem(itemId)
        })



        binding.itemsList.adapter = adapter

        myItemsViewModel.getItemsList().observe(viewLifecycleOwner, Observer {
            Log.e("itemsList", "${it}")
            adapter.submitList(it)
        })

        val adapter2 =  ItemsAdapter(ItemListener { itemId ->
            Log.e("itemId","${itemId}")
            myItemsViewModel.clickOnItem(itemId)
        })
        binding.boughtItemsList.adapter = adapter2

        myItemsViewModel.getBoughtItemsList().observe(viewLifecycleOwner, Observer {
            Log.e("boughtitemsList", "${it}")
            adapter2.submitList(it)
        })





        return binding.root
    }
}