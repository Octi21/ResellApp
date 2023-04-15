package com.example.resellapp.myItems

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentMyItemBinding

class MyItemsFragment: Fragment() {


    private lateinit var binding: FragmentMyItemBinding

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


        return binding.root
    }
}