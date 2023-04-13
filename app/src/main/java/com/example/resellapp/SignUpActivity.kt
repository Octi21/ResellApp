package com.example.resellapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.resellapp.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignUpActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivitySignUpBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("520061377985-61t4u2n9ebhq1v0pic1rqniao43vhljv.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        val localStorege = applicationContext.getSharedPreferences("LogOption", Context.MODE_PRIVATE)
        val editor = localStorege.edit()

        binding.signInButton.setOnClickListener {
            editor.putString("LoginBy","Google")
            editor.apply()
            signInGoogle()
        }


        binding.signUpButton.setOnClickListener{
            val email = binding.inputEmailText.text.toString()
            val password = binding.inputPasswordText.text.toString()
            if(checkInput())
            {
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        firebaseAuth.signOut()
                        Toast.makeText(this,"AccountCreated",Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Log.e("error", it.exception.toString())
                    }
                }
            }
        }


        binding.haveAccount.setOnClickListener{
            navToLogin()
        }

    }

    private fun navToLogin(){
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
//        finish()

    }



    private fun checkInput(): Boolean{
        val email = binding.inputEmailText.text.toString()
        val password = binding.inputPasswordText.text.toString()
        val password2 = binding.inputPasswordText2.text.toString()

        if(email == "")
        {
            binding.inputEmail.error = "This field is required"
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            binding.inputEmail.error = "Incorrect email format"
            return false
        }


        if(password == "")
        {
            binding.inputPassword.error = "This field is required"
            binding.inputPassword.endIconDrawable = null
            return false
        }
        if(password2 == "")
        {
            binding.inputPassword2.error = "This field is required"
            binding.inputPassword2.endIconDrawable = null
            return false
        }

        if(binding.inputPasswordText.length() <6)
        {
            binding.inputPassword2.error = "Required la least 6 characters"
            binding.inputPassword.endIconDrawable = null
            return false
        }

        if(password != password2)
        {
            binding.inputPassword.error = "Password do not match"

            return false
        }

        return true
    }


    // firebase google sign in
    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                val intent : Intent = Intent(this , NavigationActivity::class.java)
                intent.putExtra("email" , account.email)
                intent.putExtra("name" , account.displayName)
                startActivity(intent)
            }else{
                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()

            }
        }
    }


}