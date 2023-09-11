package com.example.resellapp.updateItem

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.resellapp.Item
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentUpdateItemBinding
import com.example.resellapp.itemDetail.ItemDetailFragmentArgs
import com.example.resellapp.itemDetail.ItemDetailFragmentDirections
import com.example.resellapp.itemDetail.ViewPagerAdapterMyItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class UpdateItemFragment: Fragment() {

    private lateinit var binding: FragmentUpdateItemBinding
    private lateinit var itemId:String

    private lateinit var updateItemViewModel: UpdateItemViewModel


    private lateinit var database: FirebaseDatabase

    private lateinit var firebaseAuth: FirebaseAuth


    private lateinit var dbRef: DatabaseReference

    private lateinit var storeRef: StorageReference

    private lateinit var ViewPageAdapter: ViewPageAdapter
    private lateinit var ViewPageAdapter2: com.example.resellapp.addItem.ViewPageAdapter
    lateinit var viewPager: ViewPager
    lateinit var viewPager2: ViewPager


    private val contract  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK) {

            cropImage(imageUri!!)
        }
    }

    private val pickImage = 100
    private val pickCamera = 200

    private var imageUri: Uri? = null
    private var imageUriList: MutableList<Uri> = mutableListOf()
    private lateinit var imageLocationList: MutableList<String>



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {


        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_update_item,container,false)

        val arguments = UpdateItemFragmentArgs.fromBundle(requireArguments())

        itemId = arguments.itemIdString

        Log.e("itemid","$itemId")


        val viewModelFactory = UpdateItemViewModelFactory(itemId)

        updateItemViewModel =
            ViewModelProvider(this, viewModelFactory).get(UpdateItemViewModel::class.java)

        binding.updateItemViewModel = updateItemViewModel


        binding.backButton.setOnClickListener {
            updateItemViewModel.navToItemDetail()
        }

        updateItemViewModel.navToItemDetail.observe(viewLifecycleOwner, Observer {
            if(it == true)
            {
                val action = UpdateItemFragmentDirections.actionUpdateItemFragmentToItemDetailFragment(itemId)
                findNavController().navigate(action)
                updateItemViewModel.deneNavigating()
            }
        })


        viewPager = binding.viewPagerId

        updateItemViewModel.imageStorageList.observe(viewLifecycleOwner, Observer {
            ViewPageAdapter = ViewPageAdapter(requireContext(),it!!,binding.countImages)
            viewPager.adapter = ViewPageAdapter
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    // Not needed for this implementation
                }

                override fun onPageSelected(position: Int) {
                    ViewPageAdapter.setPrimaryItem(viewPager, position, ViewPageAdapter.instantiateItem(viewPager, position))
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // Not needed for this implementation
                }
            })
        })

        binding.deleteButton.setOnClickListener {
            var nr: Int = binding.countImages.text[0].digitToInt() - 1

            updateItemViewModel.deleteImageAtIndex(nr)

            Log.e("ghe","${nr}")
        }

        updateItemViewModel.item.observe(viewLifecycleOwner, Observer {
            Log.e("item","${it}")
            if (it != null) {

                binding.brandText.setText(it.brand.toString())
                binding.nameText.setText(it.name.toString())
                binding.descriptionText.setText(it.description.toString())
                binding.priceText.setText(it.price!!.toInt().toString())

            }
        })





        viewPager2 = binding.viewPagerId2

        updateItemViewModel.getImageList().observe(viewLifecycleOwner, Observer {
            ViewPageAdapter2 = com.example.resellapp.addItem.ViewPageAdapter(requireContext(),it,binding.countImages2)
            viewPager2.adapter = ViewPageAdapter2

            viewPager2.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    // Not needed for this implementation
                }

                override fun onPageSelected(position: Int) {
                    ViewPageAdapter2.setPrimaryItem(viewPager2, position, ViewPageAdapter2.instantiateItem(viewPager2, position))
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // Not needed for this implementation
                }
            })


            if(it.isEmpty())
            {
                binding.viewPagerId2.visibility = View.GONE
                binding.countImages2.visibility = View.GONE
            }
            else
            {
                binding.viewPagerId2.visibility = View.VISIBLE
                binding.countImages2.visibility = View.VISIBLE

            }

        })



        database = FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app")

        dbRef = database.getReference("Items")

        storeRef = FirebaseStorage.getInstance("gs://androidkotlinresellapp.appspot.com").getReference("Images")



        binding.addButton.setOnClickListener{


            val options = arrayOf<CharSequence>("Camera", "Gallery")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose an option")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Camera option selected
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        imageUri = createImageUri()!!
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

                        contract.launch(cameraIntent)
                    }
                    1 -> {
                        // Gallery option selected
                        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                        startActivityForResult(gallery, pickImage)
                    }
                }
            }
            builder.show()
        }

        binding.confirmButton.setOnClickListener {
            if(updateObject())
            {
                Log.e("ajungemesajul","ajungemesajul")
            }
            else{
                Log.e("nuseajunge","nuseajunge")
                Toast.makeText(requireContext(),"Please fill add fields", Toast.LENGTH_LONG).show()
            }
        }



        return binding.root
    }

    private fun createImageUri(): Uri?{
        val image = File(requireContext().applicationContext.filesDir, "camera_image.png")
        return FileProvider.getUriForFile(requireContext().applicationContext,"com.example.resellapp.fileProvider",
            image)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                pickImage -> {
                    // Gallery activity result
                    imageUri = data?.data

                    cropImage(imageUri!!)
                }
                UCrop.REQUEST_CROP -> {
                    // Crop activity result
                    val croppedUri = UCrop.getOutput(data!!)
                    if (croppedUri != null) {
                        // Do something with the cropped image Uri
                        imageUri = croppedUri
//                        binding.addImage.setImageURI(croppedUri)
                        imageUriList.add(imageUri!!)
                        updateItemViewModel.addImageUri(imageUri!!)
                        for(uri in imageUriList)
                        {
                            Log.e("ImageListElem",uri.toString())
                        }
                    } else {
                        // Handle crop error
                        Toast.makeText(requireContext(), "Failed to crop image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun cropImage(imageUri: Uri) {
        // Specify the destination Uri for the cropped image
//        val imageNumber = imageUriList.size
        val timestamp: Long = System.currentTimeMillis() // Get the current timestamp

        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image$timestamp.jpg"))

        UCrop.of(imageUri, destinationUri)
            .withAspectRatio(3f, 4f) // Set the desired aspect ratio (3:4 in this case)
            .start(requireContext(), this)
    }



    suspend fun uploadPhotoToStorage(photoUri: Uri): String = withContext(Dispatchers.IO) {
        return@withContext try {
            val photoName = System.currentTimeMillis().toString()

            imageLocationList.add(photoName+ ".jpg")
            val imageRef = storeRef.child(photoName+ ".jpg")

            val uploadTask = imageRef.putFile(photoUri)
            uploadTask.await() // Wait for the upload to complete

            val imageUrlSting =imageRef.downloadUrl.await().toString()

            imageUrlSting

        } catch (e: Exception) {

            e.printStackTrace()
            throw e
        }
    }

    suspend fun uploadMultiplePhotosToStorage(photoUris: List<Uri>): List<String> = coroutineScope {
        val deferredUploads = photoUris.map { uri ->
            async {
                try {
                    uploadPhotoToStorage(uri)
                }
                catch (e: Exception) {
                    Log.e("PhotoUploadError", e.toString())
                    throw e
                }
            }
        }
        Log.e("AllUris", deferredUploads.toString())

        return@coroutineScope deferredUploads.awaitAll()
    }


    private fun updateObject(): Boolean{
        imageLocationList = mutableListOf()

        val name = binding.nameText.text.toString()
        val price = binding.priceText.text.toString()
        val description = binding.descriptionText.text.toString()
        val brand = binding.brandText.text.toString()


        if(name == "")
        {
            binding.name.error = "This field is required"

            return false
        }
        binding.name.error = null


        val price2 = try {
            price.toFloat()
        } catch (e: NumberFormatException) {
            binding.price.error = "Please enter a valid price"
            return false
        }
        binding.price.error = null

        if (price2 < 0) {
            binding.price.error = "Price cannot be negative"
            return false
        }

        val decimalPart = price2.toString().substringAfter(".")
        if (decimalPart.length > 2) {
            binding.price.error = "Price should have at most 2 decimal places"
            return false
        }


        if(description == "")
        {
            binding.description.error = "This field is required"
            return false
        }
        binding.description.error = null

        if(brand == "")
        {
            binding.name.error = "This field is required"

            return false
        }
        binding.name.error = null



        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        val listAfterDelete = updateItemViewModel.getAfterDelete()
        val listAfterDeleteLocation = updateItemViewModel.getAfterDeleteLocation()




        val itemValue = updateItemViewModel.getItem()

        updateItemViewModel.getImageList().observe(viewLifecycleOwner, Observer { imageUriList ->
            Log.e("PhotoListFinal1", imageUriList.toString())

            if (imageUriList.isNotEmpty() || listAfterDelete.isNotEmpty())
            {
                Log.e("listAfterDelete","${listAfterDelete.toString()}")
                Log.e("listAfterDeleteLocation","${listAfterDeleteLocation.toString()}")


                lifecycleScope.launch {
                    try {
                        val downloadUrls = uploadMultiplePhotosToStorage(imageUriList)

                        val timestamp: Long = System.currentTimeMillis() // Get the current timestamp

                        itemValue.price = price2
                        itemValue.name = name
                        itemValue.brand = brand
                        itemValue.description = description

                        val newUrlList = listAfterDelete + downloadUrls
                        val newLocationList = listAfterDeleteLocation + imageLocationList.toList()
                        itemValue.imageUrlList = newUrlList
                        itemValue.imageFirebaseLocations = newLocationList
                        itemValue.imageUrl = newUrlList[0]


                        dbRef.child(itemId).setValue(itemValue).addOnCompleteListener {
                            progressDialog.dismiss()
                            updateItemViewModel.deleteImages()
                            val action = UpdateItemFragmentDirections.actionUpdateItemFragmentToItemDetailFragment(itemId)
                            findNavController().navigate(action)


                            Toast.makeText(requireContext(), "Data inserted successfully", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_LONG).show()
                        }


                    } catch (e: Exception) {
                        // Handle any exceptions that occurred during the coroutine execution
                        e.printStackTrace()
                    }
                }



            }
        })


        Log.e("finished","yes")

        return true

    }



}
