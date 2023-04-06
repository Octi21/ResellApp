package com.example.resellapp.shoppingCart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentMyItemBinding
import com.example.resellapp.databinding.FragmentShoppingCartBinding

class ShoppingCartFragment: Fragment() {

    private lateinit var binding: FragmentShoppingCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_shopping_cart, container, false)

        return binding.root
    }
}