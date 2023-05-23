package com.example.resellapp.itemDetail

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
import com.bumptech.glide.Glide
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentItemDetailBinding


class ItemDetailFragment: Fragment() {

    private lateinit var binding: FragmentItemDetailBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_detail, container, false)

        val arguments = ItemDetailFragmentArgs.fromBundle(requireArguments())


        val viewModelFactory = ItemDetailViewModelFactory(arguments.itemIdString)

        val itemDetailViewModel = ViewModelProvider(this,viewModelFactory).get(ItemDetailViewModel::class.java)

        binding.itemDetailViewModel = itemDetailViewModel


        itemDetailViewModel.navToMyItems.observe(viewLifecycleOwner, Observer {
            if(it == true)
            {
                val action = ItemDetailFragmentDirections.actionItemDetailFragmentToMyItemsFragment()
                findNavController().navigate(action)
                itemDetailViewModel.doneNavigating()
            }
        })

        itemDetailViewModel.item.observe(viewLifecycleOwner, Observer {
            Log.e("item","${it}")
            if (it != null) {
                binding.name.text = it.name
                binding.price.text = it.price.toString()
                binding.description.text = it.description

                Glide.with(binding.root.context)
                    .load(it.imageUrl)
                    .override(400,300)
                    .into(binding.imageView)

            }
        })


        return binding.root

    }
}