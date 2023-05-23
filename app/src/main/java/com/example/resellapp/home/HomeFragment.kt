package com.example.resellapp.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.resellapp.Item
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentHomeBinding
import java.util.*


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


        //search bar
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!= null)
                {
                    val filteredList = ArrayList<Item>()
                    for (i in homeViewModel.getItemsList().value!!) {
                        if (i.name!!.lowercase(Locale.ROOT).contains(newText)) {
                            filteredList.add(i)
                        }
                    }

                    if (filteredList.isEmpty()) {
                        Toast.makeText(requireContext(), "No Data found", Toast.LENGTH_SHORT).show()
                    } else {
                        adapter.setFilteredList(filteredList)
                    }
                }
                return true
            }

        })



        return binding.root
    }



}

