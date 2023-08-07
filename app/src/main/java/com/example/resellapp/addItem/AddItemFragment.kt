package com.example.resellapp.addItem

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.resellapp.AspectRatioViewPager
import com.example.resellapp.Item
import com.example.resellapp.R
import com.example.resellapp.databinding.FragmentAddItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File


class AddItemFragment: Fragment() {

    private lateinit var binding: FragmentAddItemBinding

    private lateinit var database: FirebaseDatabase

    private lateinit var firebaseAuth: FirebaseAuth


    private lateinit var dbRef: DatabaseReference

    private lateinit var storeRef: StorageReference

    private val pickImage = 100
    private val pickCamera = 200

    private var imageUri: Uri? = null
    private var imageUriList: MutableList<Uri> = mutableListOf()

    lateinit var viewPager: ViewPager
    lateinit var viewPagerAdapter: ViewPageAdapter

    private lateinit var addItemViewModel: AddItemViewModel

    private var categ: Int = 0
    private var selectedButton: Button? = null




    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){
//        binding.addImage.setImageURI(null)
//        binding.addImage.setImageURI(imageUri)
        cropImage(imageUri!!)
    }





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_add_item,container,false)


        val viewModelFactory = AddItemsViewModelFactory()

        addItemViewModel = ViewModelProvider(this,viewModelFactory).get(AddItemViewModel::class.java)

        binding.addItemViewModel = addItemViewModel

        addItemViewModel.navToMyItems.observe(viewLifecycleOwner, Observer {
            if(it == true)
            {

                val action = AddItemFragmentDirections.actionAddItemFragmentToMyItemsFragment()
                findNavController().navigate(action)

                addItemViewModel.doneNavigating()
            }
        })

        binding.clothingButton.setOnClickListener {
            val defalultColor = ContextCompat.getColor(requireContext(), R.color.backgroundgrew)
            val defalultColor2 = ContextCompat.getColor(requireContext(), R.color.niceblue)

            if(categ != 1)
            {
                categ = 1
                addItemViewModel.setCategory(categ)

                it.setBackgroundColor(defalultColor2)
                binding.clothingButton2.setBackgroundColor(defalultColor)
                binding.clothingButton3.setBackgroundColor(defalultColor)

            }
            else
            {
                categ = 0
                addItemViewModel.setCategory(categ)
                it.setBackgroundColor(defalultColor)

            }


        }

        binding.clothingButton2.setOnClickListener {
            val defalultColor = ContextCompat.getColor(requireContext(), R.color.backgroundgrew)
            val defalultColor2 = ContextCompat.getColor(requireContext(), R.color.niceblue)

            if(categ != 2)
            {
                categ = 2
                addItemViewModel.setCategory(categ)

                it.setBackgroundColor(defalultColor2)
                binding.clothingButton.setBackgroundColor(defalultColor)
                binding.clothingButton3.setBackgroundColor(defalultColor)


            }
            else
            {
                categ = 0
                addItemViewModel.setCategory(categ)
                it.setBackgroundColor(defalultColor)

            }


        }

        binding.clothingButton3.setOnClickListener {
            val defalultColor = ContextCompat.getColor(requireContext(), R.color.backgroundgrew)
            val defalultColor2 = ContextCompat.getColor(requireContext(), R.color.niceblue)

            if(categ != 3)
            {
                categ = 3
                addItemViewModel.setCategory(categ)
                it.setBackgroundColor(defalultColor2)
                binding.clothingButton.setBackgroundColor(defalultColor)
                binding.clothingButton2.setBackgroundColor(defalultColor)

            }
            else
            {
                categ = 0
                addItemViewModel.setCategory(categ)
                it.setBackgroundColor(defalultColor)

            }


        }
        addItemViewModel.category.observe(viewLifecycleOwner, Observer {
            binding.categText.text = it.toString()

            val buttonContainer = binding.bouttonsLayout
            when(it){
                1 -> {
                    buttonContainer.removeAllViews()

                    val buttonNames = listOf("Jacket", "T-shirt", "Hoodie", "Pants")

                    for(name in buttonNames)
                    {
                        val button = Button(requireContext())
                        button.text = name
                        button.setBackgroundResource(R.drawable.button_subcategory)

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 24, 0, 0)
                        button.layoutParams = params

                        buttonContainer.addView(button)

                        // Set click listener for the button
                        button.setOnClickListener {
                            binding.subcategText.text = name
                            addItemViewModel.setSubcategory(name)
                            selectSubcategory(button)
                        }
                    }


                }
                2 ->{
                    buttonContainer.removeAllViews()

                    val buttonNames = listOf("Sneakers", "Shoes", "Boots", "Splippers")
                    for(name in buttonNames)
                    {
                        val button = Button(requireContext())
                        button.text = name

                        button.setBackgroundResource(R.drawable.button_subcategory)

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 24, 0, 0)
                        button.layoutParams = params

                        buttonContainer.addView(button)

                        // Set click listener for the button
                        button.setOnClickListener {
                            selectSubcategory(button)
                            addItemViewModel.setSubcategory(name)
                            binding.subcategText.text = name
                        }
                    }
                }
                3 ->{
                    buttonContainer.removeAllViews()

                    val buttonNames = listOf("Bags", "Hats", "Jewellery", "Others")
                    for(name in buttonNames)
                    {
                        val button = Button(requireContext())
                        button.text = name

                        button.setBackgroundResource(R.drawable.button_subcategory)

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 24, 0, 0)
                        button.layoutParams = params

                        buttonContainer.addView(button)

                        // Set click listener for the button
                        button.setOnClickListener {
                            selectSubcategory(button)
                            addItemViewModel.setSubcategory(name)
                            binding.subcategText.text = name
                        }
                    }
                }

                else -> {
                    buttonContainer.removeAllViews()
                }
            }
        })



        viewPager = binding.viewPagerId




        addItemViewModel.getImageList().observe(viewLifecycleOwner, Observer {
            Log.e("ImageList",it.toString())
            viewPagerAdapter = ViewPageAdapter(requireContext(),it,binding.countImages)
            viewPager.adapter = viewPagerAdapter
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    // Not needed for this implementation
                }

                override fun onPageSelected(position: Int) {
                    viewPagerAdapter.setPrimaryItem(viewPager, position, viewPagerAdapter.instantiateItem(viewPager, position))
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // Not needed for this implementation
                }
            })


            if(it.isEmpty())
            {
                binding.viewPagerId.visibility = View.GONE
                binding.countImages.visibility = View.GONE
            }
            else
            {
                binding.viewPagerId.visibility = View.VISIBLE
                binding.countImages.visibility = View.VISIBLE

            }

        })




        // onClick on xml file


        database = FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app")

        dbRef = database.getReference("Items")

        storeRef = FirebaseStorage.getInstance("gs://androidkotlinresellapp.appspot.com").getReference("Images")


        binding.addImage.setOnClickListener{


            val options = arrayOf<CharSequence>("Camera", "Gallery")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose an option")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Camera option selected

                        imageUri = createImageUri()!!
                        contract.launch(imageUri)
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



        binding.confirmButton.setOnClickListener{
            if(addObject()) {


                val action = AddItemFragmentDirections.actionAddItemFragmentToMyItemsFragment()
                findNavController().navigate(action)
            }
            else{
                Log.e("nuseajunge","nuseajunge")
            }
        }

        return binding.root
    }



    private fun selectSubcategory(button:Button){
        selectedButton?.setBackgroundResource(R.drawable.button_subcategory)

        button.setBackgroundResource(R.drawable.button_subcategory_blue)

        selectedButton = button
    }



    private fun createImageUri(): Uri?{
        val image = File(requireContext().applicationContext.filesDir, "camera_image.png")
        return FileProvider.getUriForFile(requireContext().applicationContext,"com.example.resellapp.fileProvider",
            image)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {

                pickImage -> {
                    // Gallery activity result
                    imageUri = data?.data
//                    binding.addImage.setImageURI(imageUri)
//                    Log.e("imageUri","$imageUri")

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
                        addItemViewModel.addImageUri(imageUri!!)
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
        val timestamp = System.currentTimeMillis() // Get the current timestamp

        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image$timestamp.jpg"))

        UCrop.of(imageUri, destinationUri)
            .withAspectRatio(3f, 4f) // Set the desired aspect ratio (3:5 in this case)
            .start(requireContext(), this)
    }





    suspend fun uploadPhotoToStorage(photoUri: Uri): String = withContext(Dispatchers.IO) {
        val imageRef = storeRef.child(System.currentTimeMillis().toString() + ".jpg")


        return@withContext try {
            val uploadTask = imageRef.putFile(photoUri)
            uploadTask.await() // Wait for the upload to complete
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            // Handle any potential exceptions here
            e.printStackTrace()
            ""
        }
    }

    suspend fun uploadMultiplePhotosToStorage(photoUris: List<Uri>): List<String> = coroutineScope {
        val deferredUploads = photoUris.map { uri ->
            async { uploadPhotoToStorage(uri) }
        }

        return@coroutineScope deferredUploads.awaitAll()
    }


    private fun addObject(): Boolean{

        val name = binding.nameText.text.toString()
        val price = binding.priceText.text.toString()
        val description = binding.descriptionText.text.toString()

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



        if(description == "")
        {
            binding.description.error = "This field is required"
            return false
        }
        binding.description.error = null


        //new version
        Log.e("imageUri","$imageUri")


        if (imageUri == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return false
        }

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        val itemId = dbRef.push().key!!

        addItemViewModel.getImageList().observe(viewLifecycleOwner, Observer { imageUriList ->
            if (imageUriList.isNotEmpty())
            {
                lifecycleScope.launch {
                    try {
                        val downloadUrls = uploadMultiplePhotosToStorage(imageUriList)
                        // Use the list of download URLs as needed

                        progressDialog.dismiss()

                        val item = Item(
                            itemId,
                            name,
                            price2,
                            description,
                            downloadUrls.get(0),
                            Firebase.auth.currentUser!!.uid,
                            false,
                            downloadUrls
                        )
                        dbRef.child(itemId).setValue(item).addOnCompleteListener {
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
