package com.example.resellapp.addItem

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentAddItemBinding
import com.example.resellapp.myItems.MyItemsViewModelFactory

class AddItemFragment: Fragment() {

    private lateinit var binding: FragmentAddItemBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_add_item,container,false)




        return binding.root
    }
}
