package com.example.resellapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.resellapp.databinding.ActivityMain2Binding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity2 : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)

        setContentView(binding.root)

        auth = Firebase.auth


        binding.signOutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
//        auth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("520061377985-61t4u2n9ebhq1v0pic1rqniao43vhljv.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googlesigninclient = GoogleSignIn.getClient(this,gso)
        googlesigninclient.signOut()


        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        Log.e("ghe", "am iesit")
    }

}