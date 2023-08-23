package com.example.resellapp.shoppingCart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentCheckoutSuccessBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SuccessFragment: Fragment() {

    private lateinit var binding: FragmentCheckoutSuccessBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout_success, container,false)

        GlobalScope.launch(Dispatchers.Main){
            delay(3000)
            val action = SuccessFragmentDirections.actionSuccessFragmentToShoppingCartFragment()
            findNavController().navigate(action)
        }



        return binding.root
    }
}