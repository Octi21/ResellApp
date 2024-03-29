package com.example.resellapp.itemDetailHome

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentItemDetailHomeBinding
import com.example.resellapp.databinding.ListItemHomeBinding
import com.example.resellapp.itemDetail.ItemDetailFragmentArgs
import com.example.resellapp.itemDetail.ItemDetailViewModel
import com.example.resellapp.itemDetail.ItemDetailViewModelFactory
import com.example.resellapp.itemDetail.ViewPagerAdapterMyItems
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ItemDetailHomeFragment: Fragment(){

    private lateinit var binding: FragmentItemDetailHomeBinding

    lateinit var viewPager: ViewPager
    lateinit var viewPagerAdapterHome: ViewPagerAdapterHome

    lateinit var firstImageView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_detail_home,container,false)


        val arguments = ItemDetailHomeFragmentArgs.fromBundle(requireArguments())
        val destination = arguments.destination
        Log.e("destination",destination.toString())


        val viewModelFactory = ItemDetailHomeViewModelFactory(arguments.itemIdString)

        val itemDetailHomeViewModel = ViewModelProvider(this,viewModelFactory).get(ItemDetailHomeViewModel::class.java)

        binding.itemDetailHomeViewModel = itemDetailHomeViewModel


        itemDetailHomeViewModel.navToHome.observe(viewLifecycleOwner, Observer {
            if(it != null)
            {
                if (destination == 1)
                {
                    val action = ItemDetailHomeFragmentDirections.actionItemDetailHomeFragmentToHomeFragment()
                    findNavController().navigate(action)

                }
                else
                {
                    val action = ItemDetailHomeFragmentDirections.actionItemDetailHomeFragmentToProfileFragment()
                    findNavController().navigate(action)

                }


                itemDetailHomeViewModel.doneNavigating()
            }
        })

        viewPager = binding.viewPagerId

        itemDetailHomeViewModel.item.observe(viewLifecycleOwner, Observer {
            Log.e("item","${it}")
            if (it != null) {
                binding.name.text = it.name

                val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
                formatter.applyPattern("#,##0.##")
                val formattedNumber = formatter.format(it.price)

                binding.price.text = formattedNumber.toString() + "$"
                binding.description.text = it.description
                binding.brandName.text = it.brand
                binding.details.text = it.category + " / " + it.subcategory


                viewPagerAdapterHome = ViewPagerAdapterHome(requireContext(),it.imageUrlList!!,binding.countImages)

                viewPager.adapter = viewPagerAdapterHome
                viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                        // Not needed for this implementation
                    }

                    override fun onPageSelected(position: Int) {
                        viewPagerAdapterHome.setPrimaryItem(viewPager, position, viewPagerAdapterHome.instantiateItem(viewPager, position))
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        // Not needed for this implementation
                    }
                })

                Glide.with(binding.root.context).load(it.imageUrl).into(binding.imageView)

                firstImageView = viewPagerAdapterHome.firstImage?: binding.imageView


            }
        })



        itemDetailHomeViewModel.showToast.observe(viewLifecycleOwner, Observer {
            if(it == true){
                Toast.makeText(requireContext(),"Already added",Toast.LENGTH_SHORT).show()
                itemDetailHomeViewModel.doneToast()
            }
        })
        itemDetailHomeViewModel.addTast.observe(viewLifecycleOwner, Observer {
            if(it == true){
                Toast.makeText(requireContext(),"Item added To Cart",Toast.LENGTH_SHORT).show()
                itemDetailHomeViewModel.doneToast()
            }
        })
        //onClick on xml


        itemDetailHomeViewModel.liked.observe(viewLifecycleOwner, Observer {
            if(it ==true)
            {
                binding.likedButton.visibility = View.VISIBLE
                binding.likedborderButton.visibility = View.GONE
            }
        })


        binding.likedborderButton.setOnClickListener {

            itemDetailHomeViewModel.addItemToLiked()
            binding.likedButton.visibility = View.VISIBLE
            binding.likedborderButton.visibility = View.GONE

        }

        binding.likedButton.setOnClickListener {

            itemDetailHomeViewModel.addItemToLiked()
            binding.likedButton.visibility = View.GONE
            binding.likedborderButton.visibility = View.VISIBLE

        }




        binding.shareButton.setOnClickListener {
//            val imageUri = getImageUri(binding.imageView)
            val text = "Check out this item\n" +
                    "Name: ${binding.name.text} \n" +
                    "Price: ${binding.price.text} \n" +
                    "Description: ${binding.description.text} \n"



            val imageUri = getImageUri(firstImageView)
            sharePhotoAndText(imageUri!!, text)

        }

        binding.addToCart.setOnClickListener {
            Toast.makeText(requireContext(),"Added to yout Cart",Toast.LENGTH_SHORT).show()
            Log.e("click","yes")
        }






        return binding.root
    }

    private fun sharePhotoAndText(photoUri: Uri, text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, photoUri)
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareChooser = Intent.createChooser(shareIntent, "Share Photo")
        startActivity(shareChooser)
    }

    fun getImageUri(imageView: ImageView): Uri? {
        val drawable = imageView.drawable
        val bitmap: Bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }

        var uri: Uri? = null
        try {
            val cachePath = File(requireContext().applicationContext.filesDir, "images")

            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/image.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            uri = FileProvider.getUriForFile(imageView.context,
                "com.example.resellapp.fileProvider",
                File("$cachePath/image.png"))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return uri
    }
}