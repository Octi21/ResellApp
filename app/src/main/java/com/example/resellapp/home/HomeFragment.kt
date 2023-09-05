package com.example.resellapp.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.Visibility
import com.example.resellapp.Item
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class HomeFragment: Fragment() {



    @SuppressLint("ClickableViewAccessibility")
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
            homeViewModel.clickOnItem(itemId)
        },1)

        binding.itemsList.adapter = adapter

        homeViewModel.getItemsList1().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)

            viewLifecycleOwner.lifecycleScope.launch {
                delay(100) // Delay for 1 second (1000 milliseconds)
                binding.itemsList.layoutManager?.scrollToPosition(0)
            }

        })


        val adapter2 = ItemsHomeAdapter(ItemHomeListener { itemId ->
            Log.e("itemId","${itemId}")
            homeViewModel.clickOnItem(itemId)
        },2)
        val adapter3 = ItemsHomeAdapter(ItemHomeListener { itemId ->
            Log.e("itemId","${itemId}")
            homeViewModel.clickOnItem(itemId)
        },2)
        val adapter4 = ItemsHomeAdapter(ItemHomeListener { itemId ->
            Log.e("itemId","${itemId}")
            homeViewModel.clickOnItem(itemId)
        },2)

        binding.clothingItemsList.adapter = adapter2
        binding.footwearItemsList.adapter = adapter3
        binding.accessoriesItemsList.adapter = adapter4



        homeViewModel.itemsListClothing.observe(viewLifecycleOwner, Observer {
            Log.e("itemsListc", "${it}")
            adapter2.submitList(it)

            viewLifecycleOwner.lifecycleScope.launch {
                delay(100) // Delay for 1 second (1000 milliseconds)
                binding.clothingItemsList.layoutManager?.scrollToPosition(0)
            }

        })

        homeViewModel.itemsListFootwear.observe(viewLifecycleOwner, Observer {
            adapter3.submitList(it)

            viewLifecycleOwner.lifecycleScope.launch {
                delay(100) // Delay for 1 second (1000 milliseconds)
                binding.footwearItemsList.layoutManager?.scrollToPosition(0)
            }
        })
        homeViewModel.itemsListAcc.observe(viewLifecycleOwner, Observer {
            adapter4.submitList(it)

            viewLifecycleOwner.lifecycleScope.launch {
                delay(100) // Delay for 1 second (1000 milliseconds)
                binding.accessoriesItemsList.layoutManager?.scrollToPosition(0)
            }
        })



        //search bar
        binding.searchBar.queryHint = "Name,Brand"

        homeViewModel.hideFilters.observe(viewLifecycleOwner, Observer {
            if(it == true)
            {
                binding.filterButton.visibility = View.GONE
            }
            else
            {
                binding.filterButton.visibility = View.VISIBLE

            }
        })

        binding.searchBar.setOnCloseListener(object :SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                homeViewModel.setHideFilters(false)
                return false

            }
        })


        binding.searchBar.setOnSearchClickListener  {
            homeViewModel.setHideFilters(true)
            binding.refineLayout.visibility = View.GONE


        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                homeViewModel.setHideFilters(true)
                if(newText!= null)
                {
                    val filteredList = ArrayList<Item>()
                    for (i in homeViewModel.getItemsList1().value!!) {
                        if (i.name!!.lowercase(Locale.ROOT).contains(newText.lowercase(Locale.ROOT))) {
                            filteredList.add(i)
                        }
                    }
                    if (filteredList.isEmpty()) {
                        adapter.setFilteredList(filteredList)
                    } else {
                        adapter.setFilteredList(filteredList)
                    }
                }
                return true
            }

        })

        binding.exitRefineView.setOnClickListener {
            binding.refineLayout.visibility = View.GONE


        }
        binding.filterButton.setOnClickListener {
            binding.refineLayout.visibility = View.VISIBLE



        }


        homeViewModel.RBvalue.observe(viewLifecycleOwner, Observer {
            Log.e("RBVALUE","$it")
            when(it){
                2 -> binding.newRB.isChecked = true
                3 -> binding.priceHighRB.isChecked = true
                4 -> binding.priceLowRB.isChecked = true
                else -> binding.featureRB.isChecked = true

            }

        })


        binding.finish.setOnClickListener {
            binding.refineLayout.visibility = View.GONE
            homeViewModel.ResetFirstList()

            if(binding.featureRB.isChecked)
            {
                homeViewModel.changeRBvalue(1)

                homeViewModel.ResetFirstList()
            }

            val checkboxList: List<CheckBox> = listOf(binding.CB43,binding.CB44,binding.CB45,binding.CB46,binding.CBbags,binding.CBJacket,
                binding.CBboots,binding.CBhats,binding.CBhoodie,binding.CBjewellery,binding.CBothers,binding.CBpants,
                binding.CBshoes,binding.CBslippers,binding.CBsneakers,binding.CBtshirt)

            val checkboxSizeList: List<CheckBox> = listOf(binding.CB43,binding.CB44,binding.CB45,binding.CB46,binding.CB35,
                binding.CB36,binding.CB37,binding.CB38,binding.CB39,binding.CB40,binding.CB41,binding.CB42,binding.CBnosize,
                binding.CBxs,binding.CBs,binding.CBm,binding.CBl,binding.CBxl,binding.CBxxl)

            val list: MutableList<String> = emptyList<String>().toMutableList()

            for(elem in checkboxList)
            {
                if(elem.isChecked)
                    list.add(elem.text.toString())
            }


            val listSize: MutableList<String> = emptyList<String>().toMutableList()

            for(elem in checkboxSizeList)
            {
                if(elem.isChecked)
                    listSize.add(elem.text.toString())
            }



            if(!list.isEmpty())
            {
                Log.e("list","$list")
                homeViewModel.setSubcatList(list.toList())

                homeViewModel.filterListSubateg()
            }
            if(!listSize.isEmpty())
            {
                Log.e("list","$listSize")
                homeViewModel.setSizeList(listSize.toList())

                homeViewModel.filterListSize()
            }



            if(binding.priceLowRB.isChecked)
            {
                homeViewModel.changeRBvalue(4)
                homeViewModel.listByPriceAsc()
            }
            if(binding.priceHighRB.isChecked)
            {
                homeViewModel.changeRBvalue(3)

                homeViewModel.listByPriceDesc()
            }
            if(binding.newRB.isChecked)
            {
                homeViewModel.changeRBvalue(2)

                homeViewModel.listByDate()
            }




        }
        var sizeBtn = false

        binding.sizeButton.setOnClickListener {

            sizeBtn = !sizeBtn
            if(sizeBtn)
            {
                binding.arrow1size.visibility = View.GONE
                binding.arrow2size.visibility = View.VISIBLE
                binding.sizeFrame.visibility = View.VISIBLE

            }
            else
            {
                binding.arrow1size.visibility = View.VISIBLE
                binding.arrow2size.visibility = View.GONE
                binding.sizeFrame.visibility = View.GONE
            }

        }
        var cloBtn = false

        binding.clothingButton.setOnClickListener {

            cloBtn = !cloBtn
            if(cloBtn)
            {
                binding.arrow1clothing.visibility = View.GONE
                binding.arrow2clothing.visibility = View.VISIBLE
                binding.clothingFrame.visibility = View.VISIBLE

            }
            else
            {
                binding.arrow1clothing.visibility = View.VISIBLE
                binding.arrow2clothing.visibility = View.GONE
                binding.clothingFrame.visibility = View.GONE
            }

        }

        var fooBtn = false

        binding.footwearButton.setOnClickListener {

            fooBtn = !fooBtn
            if(fooBtn)
            {
                binding.arrow1footwear.visibility = View.GONE
                binding.arrow2footwear.visibility = View.VISIBLE
                binding.footwearFrame.visibility = View.VISIBLE

            }
            else
            {
                binding.arrow1footwear.visibility = View.VISIBLE
                binding.arrow2footwear.visibility = View.GONE
                binding.footwearFrame.visibility = View.GONE
            }

        }

        var accBtn = false

        binding.accessoriesButton.setOnClickListener {

            accBtn = !accBtn
            if(accBtn)
            {
                binding.arrow1accessories.visibility = View.GONE
                binding.arrow2accessories.visibility = View.VISIBLE
                binding.accessoriesFrame.visibility = View.VISIBLE

            }
            else
            {
                binding.arrow1accessories.visibility = View.VISIBLE
                binding.arrow2accessories.visibility = View.GONE
                binding.accessoriesFrame.visibility = View.GONE
            }

        }

        binding.resetFilters.setOnClickListener {
            homeViewModel.ResetFirstList()
            val checkboxList: List<CheckBox> = listOf(binding.CB43,binding.CB44,binding.CB45,binding.CB46,binding.CB35,
                binding.CB36,binding.CB37,binding.CB38,binding.CB39,binding.CB40,binding.CB41,binding.CB42,binding.CBnosize,
                binding.CBxs,binding.CBs,binding.CBm,binding.CBl,binding.CBxl,binding.CBxxl,binding.CBbags,binding.CBJacket,
                binding.CBboots,binding.CBhats,binding.CBhoodie,binding.CBjewellery,binding.CBothers,binding.CBpants,
                binding.CBshoes,binding.CBslippers,binding.CBsneakers,binding.CBtshirt)

            for(elem in checkboxList)
                elem.isChecked = false

            binding.featureRB.isChecked = true
            binding.newRB.isChecked = false
            binding.priceHighRB.isChecked = false
            binding.priceLowRB.isChecked = false

            homeViewModel.setSubcatList(emptyList())
            homeViewModel.changeRBvalue(1)


            binding.arrow1footwear.visibility = View.VISIBLE
            binding.arrow2footwear.visibility = View.GONE
            binding.footwearFrame.visibility = View.GONE
            binding.arrow1accessories.visibility = View.VISIBLE
            binding.arrow2accessories.visibility = View.GONE
            binding.accessoriesFrame.visibility = View.GONE
            binding.arrow1clothing.visibility = View.VISIBLE
            binding.arrow2clothing.visibility = View.GONE
            binding.clothingFrame.visibility = View.GONE
            binding.arrow1size.visibility = View.VISIBLE
            binding.arrow2size.visibility = View.GONE
            binding.sizeFrame.visibility = View.GONE

            binding.refineLayout.visibility = View.GONE


        }





        return binding.root
    }



}

