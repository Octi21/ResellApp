package com.example.resellapp.myItems

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentMyItemBinding

class MyItemsFragment: Fragment() {


    private lateinit var binding: FragmentMyItemBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_my_item,container,false)

        return binding.root
    }
}