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
import com.example.resellapp.LoginActivity
import com.example.resellapp.R
import com.example.resellapp.SignUpActivity
import com.example.resellapp.databinding.FragmentProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment: Fragment() {

    private lateinit var loginMethod: String

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_profile,container,false)

        val localStorege = requireContext().getSharedPreferences("LogOption", Context.MODE_PRIVATE)

        loginMethod = localStorege.getString("LoginBy","").toString()

        binding.textView3.text = loginMethod

        binding.signOutButton.setOnClickListener{
            signOut()
        }

        return binding.root
    }

    private fun signOut() {

        if (loginMethod.equals("Gmail")) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("520061377985-61t4u2n9ebhq1v0pic1rqniao43vhljv.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googlesigninclient = GoogleSignIn.getClient(requireContext(), gso)
            googlesigninclient.signOut()


            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        else if ((loginMethod.equals("Email"))) {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        else
        {
            Log.e("error","signOut error")
        }
    }
}