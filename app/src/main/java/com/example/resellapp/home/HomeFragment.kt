package com.example.resellapp.home

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
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentHomeBinding
import com.example.resellapp.myItems.ItemListener
import com.example.resellapp.myItems.MyItemsFragmentDirections

class HomeFragment: Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,container,false)


        val viewModelFactory = HomeViewModelFactory()

        val homeViewModel = ViewModelProvider(this,viewModelFactory).get(HomeViewModel::class.java)

        binding.homeViewModel = homeViewModel



        //nav to detail onClick
        homeViewModel.navigateToItemDetail.observe(viewLifecycleOwner, Observer {
            if (it != null)
            {
                val action = HomeFragmentDirections.actionHomeFragmentToItemDetailHomeFragment(it)
                findNavController().navigate(action)

                homeViewModel.doneNavigatingDetails()
            }
        })


        //adapter

        val adapter = ItemsHomeAdapter(ItemHomeListener { itemId ->
            Log.e("itemId","${itemId}")
            homeViewModel.clickOnItem(itemId)
        })


        binding.itemsList.adapter = adapter

        homeViewModel.getItemsList().observe(viewLifecycleOwner, Observer {
            Log.e("itemsList", "${it}")
            adapter.submitList(it)
        })


        return binding.root
    }
}