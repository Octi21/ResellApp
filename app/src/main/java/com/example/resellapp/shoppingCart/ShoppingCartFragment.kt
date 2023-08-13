package com.example.resellapp.shoppingCart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentMyItemBinding
import com.example.resellapp.databinding.FragmentShoppingCartBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ShoppingCartFragment: Fragment() {

    private lateinit var binding: FragmentShoppingCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_shopping_cart, container, false)

        val viewModelFactory = ShoppingCartViewModelFactory()

        val shoppingCartViewModel = ViewModelProvider(this,viewModelFactory).get(ShoppingCartViewModel::class.java)

        binding.shoppingCartViewModel = shoppingCartViewModel




        val adapter = CartAdapter(shoppingCartViewModel)

        binding.itemsList.adapter = adapter

        shoppingCartViewModel.getItemsList().observe(viewLifecycleOwner, Observer {
            Log.e("cartList", "${it}")
            Log.e("size","${it.size}")
            val size = it.size
            if(size ==1)
                binding.title.text = "Shopping Bag (1 item)"
            if(size == 0)
                binding.title.text = "Shopping Bag (empty)"
            else
                binding.title.text = "Shopping Bag ($size items)"
            var sum = 0.0
            for(item in it)
                sum += item.price!!

            val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
            formatter.applyPattern("#,##0.##")
            val formattedNumber = formatter.format(sum)

            binding.subtotal.text = formattedNumber.toString() + "$"


            adapter.submitList(it)
        })

        return binding.root
    }
}