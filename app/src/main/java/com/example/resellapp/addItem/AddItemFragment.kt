package com.example.resellapp.addItem

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.camera2.CameraCharacteristics
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.resellapp.ButtonItem
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

    private var sizeButton: Button? = null

    private lateinit var imageLocationList: MutableList<String>


    private val contract2  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK) {

            cropImage(imageUri!!)
        }
    }


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

        val defalultColor = ContextCompat.getColor(requireContext(), R.color.backgroundgrew)
        val defalultColor2 = ContextCompat.getColor(requireContext(), R.color.niceblue)

        categ = when(addItemViewModel.getCatogory()){
            "Clothing" -> 1
            "Footwear" -> 2
            "Accessories" ->3
            else -> 0

        }

        if(categ == 1)
        {
            binding.clothingButton.setBackgroundColor(defalultColor2)
        }
        if(categ == 2)
        {
            binding.clothingButton2.setBackgroundColor(defalultColor2)
        }
        if(categ == 3)
        {
            binding.clothingButton3.setBackgroundColor(defalultColor2)
        }

        binding.clothingButton.setOnClickListener {


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
                addItemViewModel.setSubcategory("")
                addItemViewModel.setSize("")
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
                addItemViewModel.setSubcategory("")
                addItemViewModel.setSize("")
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
                addItemViewModel.setSubcategory("")
                addItemViewModel.setSize("")
                it.setBackgroundColor(defalultColor)

            }


        }

        addItemViewModel.category.observe(viewLifecycleOwner, Observer {

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
                        }
                    }
                }

                else -> {
                    buttonContainer.removeAllViews()
                }
            }

            if(it != 0)
            {
                binding.size.visibility = View.VISIBLE
                binding.sizeLayout.visibility = View.VISIBLE
            }
            else{
                binding.size.visibility = View.GONE
                binding.sizeLayout.visibility = View.GONE
            }
            val sizeLayout = binding.sizeLayout
            when(it){
                1 ->{


                    sizeLayout.removeAllViews()

                    val buttonNames = listOf("XS", "S", "M", "L", "XL", "XXL")
                    for(name in buttonNames)
                    {
                        val button = Button(requireContext())
                        button.text = name

                        button.setBackgroundResource(R.drawable.button_subcategory)

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 24, 24, 0)
                        button.layoutParams = params

                        sizeLayout.addView(button)

                        // Set click listener for the button
                        button.setOnClickListener {
                            selectSubcategorySize(button)
                            addItemViewModel.setSize(name)
                        }
                    }
                }
                2->{
                    sizeLayout.removeAllViews()

                    val buttonNames = listOf("35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46")
                    for(name in buttonNames)
                    {
                        val button = Button(requireContext())
                        button.text = name

                        button.setBackgroundResource(R.drawable.button_subcategory)

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 24, 24, 0)
                        button.layoutParams = params

                        sizeLayout.addView(button)

                        // Set click listener for the button
                        button.setOnClickListener {
                            selectSubcategorySize(button)
                            addItemViewModel.setSize(name)
                        }
                    }
                }
                3->{
                    sizeLayout.removeAllViews()

                    val buttonNames = listOf("XS", "S", "M", "L", "XL", "XXL","35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46","No Size")
                    for(name in buttonNames)
                    {
                        val button = Button(requireContext())
                        button.text = name

                        button.setBackgroundResource(R.drawable.button_subcategory)

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 24, 24, 0)
                        button.layoutParams = params

                        sizeLayout.addView(button)

                        // Set click listener for the button
                        button.setOnClickListener {
                            selectSubcategorySize(button)
                            addItemViewModel.setSize(name)
                        }
                    }
                }
                else ->{
                    sizeLayout.removeAllViews()
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



//        binding.priceText.setText("1")
//        binding.brandText.setText("1")
//        binding.nameText.setText("1")
//        binding.descriptionText.setText("1")

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
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        imageUri = createImageUri()!!
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

                        contract2.launch(cameraIntent)
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
                Log.e("ajungemesajul","ajungemesajul")


//                val action = AddItemFragmentDirections.actionAddItemFragmentToMyItemsFragment()
//                findNavController().navigate(action)
            }
            else{
                Log.e("nuseajunge","nuseajunge")
                Toast.makeText(requireContext(),"Please fill add fields", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }



    private fun selectSubcategory(button:Button){
        selectedButton?.setBackgroundResource(R.drawable.button_subcategory)

        button.setBackgroundResource(R.drawable.button_subcategory_blue)

        selectedButton = button
    }
    private fun selectSubcategorySize(button:Button){
        sizeButton?.setBackgroundResource(R.drawable.button_subcategory)

        button.setBackgroundResource(R.drawable.button_subcategory_blue)

        sizeButton = button
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

//    suspend fun uploadMultiplePhotosToStorage(photoUris: List<Uri>): List<String> = coroutineScope {
//        val deferredUploads = photoUris.map { uri ->
//            async {
//                try {
//                uploadPhotoToStorage(uri)
//                }
//                catch (e: Exception) {
//                    Log.e("PhotoUploadError", e.toString())
//                    throw e
//                }
//            }
//        }
//        Log.e("AllUris", deferredUploads.toString())
//
//        return@coroutineScope deferredUploads.awaitAll()
//    }

    suspend fun uploadMultiplePhotosToStorage(photoUris: List<Uri>): List<String> {
        val uploadedUrls = mutableListOf<String>()

        for (uri in photoUris) {
            try {
                val imageUrl = uploadPhotoToStorage(uri)
                uploadedUrls.add(imageUrl)
            } catch (e: Exception) {
                Log.e("PhotoUploadError", e.toString())
                throw e
            }
        }

        return uploadedUrls
    }


    private fun addObject(): Boolean{
        imageLocationList = mutableListOf()

        val name = binding.nameText.text.toString()
        val price = binding.priceText.text.toString()
        val description = binding.descriptionText.text.toString()
        val brand = binding.brandText.text.toString()

        val category = addItemViewModel.getCatogory()
        val subcatogory = addItemViewModel.getSubcatogory()
        val size = addItemViewModel.getSize()

        if(categ == 0)
        {
            return false
        }
        else
        {
            if(subcatogory == "" || size == "")
                return false
        }

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
            Log.e("PhotoListFinal1", imageUriList.toString())

            if (imageUriList.isNotEmpty())
            {
                lifecycleScope.launch {
                    try {
                        val downloadUrls = uploadMultiplePhotosToStorage(imageUriList)

                        val timestamp: Long = System.currentTimeMillis() // Get the current timestamp

                        val item = Item(
                            itemId,
                            name,
                            price2,
                            description,
                            downloadUrls.get(0),
                            Firebase.auth.currentUser!!.uid,
                            false,
                            downloadUrls,
                            brand,
                            category,
                            subcatogory,
                            size,
                            timestamp,
                            imageLocationList.toList()
                        )
                        dbRef.child(itemId).setValue(item).addOnCompleteListener {
                            progressDialog.dismiss()
                            val action = AddItemFragmentDirections.actionAddItemFragmentToMyItemsFragment()
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
