package com.example.resellapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.resellapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity:  AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.hide()


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

        binding.signInWithEmailButton.setOnClickListener{
            editor.putString("LoginBy","Email")
            editor.apply()

            val email = binding.inputEmailText.text.toString()
            val password = binding.inputPasswordText.text.toString()
            if(checkInput())
            {
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful)
                    {

                        val verification  = firebaseAuth.currentUser?.isEmailVerified
                        if(verification == true)
                        {
                            editor.putString("email",email)
                            editor.apply()
                            val intent =Intent(this,NavigationActivity::class.java)
                            startActivity(intent)

                            //destroy activity
                        // finish()
                        }
                        else
                        {
                            Toast.makeText(this,"Please verify email",Toast.LENGTH_SHORT).show()
                        }

                    }
                    else
                    {
                        Log.e("error",it.exception.toString())
                    }
                }
            }
        }
        binding.createAcount.setOnClickListener{
            navToSignUp()
        }

    }

    private fun navToSignUp(){
        val intent = Intent(this,SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkInput(): Boolean{
        val email = binding.inputEmailText.text.toString()
        val password = binding.inputPasswordText.text.toString()

        if(email == "")
        {
            binding.inputEmail.error = "This field is required"
            return false
        }
        binding.inputEmail.error = null

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            binding.inputEmail.error = "Incorrect email format"
            return false
        }
        binding.inputEmail.error = null


        if(password == "")
        {
            binding.inputPassword.error = "This field is required"
            binding.inputPassword.endIconDrawable = null
            return false
        }
        binding.inputPassword.error = null


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
//                intent.putExtra("email" , account.email)
//                intent.putExtra("name" , account.displayName)

                val localStorege = applicationContext.getSharedPreferences("LogOption", Context.MODE_PRIVATE)
                val editor = localStorege.edit()

                editor.putString("email",account.email)
                editor.putString("name",account.displayName)
                editor.apply()

                startActivity(intent)
            }else{
                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()

            }
        }
    }
}