package com.example.resellapp.itemDetailHome

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
import com.bumptech.glide.Glide
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentItemDetailHomeBinding
import com.example.resellapp.databinding.ListItemHomeBinding
import com.example.resellapp.itemDetail.ItemDetailFragmentArgs
import com.example.resellapp.itemDetail.ItemDetailViewModel
import com.example.resellapp.itemDetail.ItemDetailViewModelFactory

class ItemDetailHomeFragment: Fragment(){

    private lateinit var binding: FragmentItemDetailHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_detail_home,container,false)


        val arguments = ItemDetailHomeFragmentArgs.fromBundle(requireArguments())


        val viewModelFactory = ItemDetailHomeViewModelFactory(arguments.itemIdString)

        val itemDetailHomeViewModel = ViewModelProvider(this,viewModelFactory).get(ItemDetailHomeViewModel::class.java)

        binding.itemDetailHomeViewModel = itemDetailHomeViewModel


        itemDetailHomeViewModel.navToHome.observe(viewLifecycleOwner, Observer {
            if(it != null)
            {
                val action = ItemDetailHomeFragmentDirections.actionItemDetailHomeFragmentToHomeFragment()
                findNavController().navigate(action)

                itemDetailHomeViewModel.doneNavigating()
            }
        })

        itemDetailHomeViewModel.item.observe(viewLifecycleOwner, Observer {
            Log.e("item","${it}")
            if (it != null) {
                binding.name.text = it.name
                binding.price.text = it.price.toString()

                Glide.with(binding.root.context).load(it.imageUrl).into(binding.imageView)

            }
        })



        itemDetailHomeViewModel.showToast.observe(viewLifecycleOwner, Observer {
            if(it == true){
                Toast.makeText(requireContext(),"Already added",Toast.LENGTH_SHORT).show()
                itemDetailHomeViewModel.doneToast()
            }
        })
        //onClick on xml








        return binding.root
    }
}