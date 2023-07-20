package com.example.resellapp.addItem

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
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

//        if(imageUri != null)
//        {
//            val progressDialog = ProgressDialog(requireContext())
//            progressDialog.setTitle("Uploading...")
//            progressDialog.show()
//
//
//            val imageRef = storeRef.child(System.currentTimeMillis().toString() + ".jpg")
//
//            imageRef.putFile(imageUri!!).addOnSuccessListener {
//
//                progressDialog.dismiss()
//                Toast.makeText(requireContext(), "Image uploaded", Toast.LENGTH_SHORT).show()
//
//                val itemId = dbRef.push().key!!
//
//                imageRef.downloadUrl.addOnSuccessListener {
//                    val url = it.toString()
//                    Log.e("photolink","$url")
//
//                    val item = Item(itemId,name,price2,description,url, Firebase.auth.currentUser!!.uid)
//
//                    dbRef.child(itemId).setValue(item).addOnCompleteListener{
//                        Toast.makeText(requireContext(),"Data inserted Success", Toast.LENGTH_LONG).show()
//                    }.addOnFailureListener {
//                        Toast.makeText(requireContext(),"Error ${it.message}", Toast.LENGTH_LONG).show()
//
//                    }
//
//                }
//
////                val url = it.storage.downloadUrl.toString()
////                Log.e("photolink3","${imageUri}")
////                Log.e("photolink2", "${it.toString()}")
////                Log.e("photolink","${url}")
//
//
//
//            }
//                .addOnFailureListener {
//                    progressDialog.dismiss()
//                    Toast.makeText(requireContext(), "Failed " + it.message, Toast.LENGTH_SHORT).show()
//                }
//                .addOnProgressListener {
//                    val progress = 100.0 * it.bytesTransferred / it.totalByteCount
//                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
//                }
//
//        }
//        else{
//            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
//            return false
//        }



        Log.e("finished","yes")

        return true

    }
}
