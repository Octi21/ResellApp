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
import com.bumptech.glide.Glide
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentItemDetailHomeBinding
import com.example.resellapp.databinding.ListItemHomeBinding
import com.example.resellapp.itemDetail.ItemDetailFragmentArgs
import com.example.resellapp.itemDetail.ItemDetailViewModel
import com.example.resellapp.itemDetail.ItemDetailViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
                binding.description.text = it.description


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




        binding.shareButton.setOnClickListener {
            val imageUri = getImageUri(binding.imageView)
            val text = "Check out this item\n" +
                    "Name: ${binding.name.text} \n" +
                    "Price: ${binding.price} \n" +
                    "Description: ${binding.description} \n"
            sharePhotoAndText(imageUri!!, text)

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