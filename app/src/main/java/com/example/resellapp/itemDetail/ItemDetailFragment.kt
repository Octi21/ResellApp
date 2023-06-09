package com.example.resellapp.itemDetail

import android.app.AlertDialog
import android.content.DialogInterface
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
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentItemDetailBinding


class ItemDetailFragment: Fragment() {

    private lateinit var binding: FragmentItemDetailBinding


    lateinit var viewPager: ViewPager
    lateinit var ViewPagerAdapterMyItems: ViewPagerAdapterMyItems

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_detail, container, false)

        val arguments = ItemDetailFragmentArgs.fromBundle(requireArguments())


        val viewModelFactory = ItemDetailViewModelFactory(arguments.itemIdString)

        val itemDetailViewModel = ViewModelProvider(this,viewModelFactory).get(ItemDetailViewModel::class.java)

        binding.itemDetailViewModel = itemDetailViewModel

        viewPager = binding.viewPagerId


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

                if(it.bought == true)
                {
                    binding.deleteButton.visibility = View.GONE
                }
                else
                {
                    binding.deleteButton.visibility = View.VISIBLE
                }


                Glide.with(binding.root.context)
                    .load(it.imageUrl)
                    .override(400,300)
                    .into(binding.imageView)

                ViewPagerAdapterMyItems = ViewPagerAdapterMyItems(requireContext(),it.imageUrlList!!,binding.countImages)
                viewPager.adapter = ViewPagerAdapterMyItems
                viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                        // Not needed for this implementation
                    }

                    override fun onPageSelected(position: Int) {
                        ViewPagerAdapterMyItems.setPrimaryItem(viewPager, position, ViewPagerAdapterMyItems.instantiateItem(viewPager, position))
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        // Not needed for this implementation
                    }
                })

            }
        })


        binding.deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to delete?")
            builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                itemDetailViewModel.deleteItem()
                dialogInterface.dismiss()
            }
            builder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                // Handle negative button click
                dialogInterface.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }


        return binding.root

    }


}