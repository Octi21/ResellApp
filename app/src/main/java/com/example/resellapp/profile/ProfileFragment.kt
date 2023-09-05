package com.example.resellapp.profile

import android.content.Context
import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.example.resellapp.LoginActivity
import com.example.resellapp.R
import com.example.resellapp.SignUpActivity
import com.example.resellapp.databinding.FragmentProfileBinding
import com.example.resellapp.home.HomeFragmentDirections
import com.example.resellapp.home.ItemHomeListener
import com.example.resellapp.home.ItemsHomeAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment: Fragment() {

    private lateinit var loginMethod: String

    private lateinit var binding: FragmentProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var userName: String
    private lateinit var userEmail: String
    private lateinit var userType: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_profile,container,false)

        val viewModelFactory = ProfileViewModelFactory()
        val profileViewModel = ViewModelProvider(this,viewModelFactory).get(ProfileViewModel::class.java)

        binding.profileViewModel = profileViewModel




        val localStorege = requireContext().getSharedPreferences("LogOption", Context.MODE_PRIVATE)

        loginMethod = localStorege.getString("LoginBy","").toString()

        binding.textView3.text = loginMethod

        binding.email.text = binding.email.text.toString() + localStorege.getString("email","").toString()

        if (loginMethod.equals("Google"))
        {
            binding.name.text = binding.name.text.toString() + localStorege.getString("name","").toString()
        }

        firebaseAuth = Firebase.auth
        userName = firebaseAuth.currentUser?.displayName ?: ""
        userEmail = firebaseAuth.currentUser?.email ?: ""
        userType = "Google"
        if(userName == "") {
            Log.e("ghe", "ghe")
            userType = "Email"
        }

        Log.e("ghe","$userEmail +  $userType")



        //nav to detail onClick
        profileViewModel.navigateToItemDetail.observe(viewLifecycleOwner, Observer {
            if (it != null)
            {
                val action = ProfileFragmentDirections.actionProfileFragmentToItemDetailHomeFragment(it)
                findNavController().navigate(action)

                profileViewModel.doneNavigatingDetails()
            }
        })

        //adapter

        val adapter = ItemsHomeAdapter(ItemHomeListener { itemId ->
            Log.e("itemId","${itemId}")
            profileViewModel.clickOnItem(itemId)
        },2)

        binding.likedItemsList.adapter = adapter

        profileViewModel.likeditemsList.observe(viewLifecycleOwner, Observer {
            Log.e("likedItems",it.toString())
            adapter.submitList(it)
        })

        profileViewModel.deletedList.observe(viewLifecycleOwner, Observer {
            Log.e("deletedItems",it.toString())
            profileViewModel.deleteIfInList(it)
        })



        binding.signOutButton.setOnClickListener{
            signOut()
        }

        return binding.root
    }






    private fun signOut() {

        if (userType.equals("Google")) {
            Log.e("logout","google")
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("520061377985-61t4u2n9ebhq1v0pic1rqniao43vhljv.apps.googleusercontent.com")
                .requestEmail()
                .build()

            FirebaseAuth.getInstance().signOut()
            val googlesigninclient = GoogleSignIn.getClient(requireContext(), gso)
            googlesigninclient.signOut()


            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }
        else if ((userType.equals("Email"))) {
            Log.e("logout","email")

            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }
        else
        {
            Log.e("error","signOut error")
        }
    }
}