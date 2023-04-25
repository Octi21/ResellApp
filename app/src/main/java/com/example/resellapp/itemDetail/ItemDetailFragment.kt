package com.example.resellapp.itemDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentItemDetailBinding

class ItemDetailFragment: Fragment() {

    private lateinit var binding: FragmentItemDetailBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_detail, container, false)

        return binding.root

    }
}