package com.example.resellapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.resellapp.databinding.ActivityNavigationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


class NavigationActivity: AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var firebaseAuth: FirebaseAuth


    private lateinit var database: FirebaseDatabase

    private lateinit var dbRef: DatabaseReference




    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        drawerLayout = binding.drawerLayout

        setContentView(binding.root)



        val bundle = Bundle()
        bundle.putString("email", intent.getStringExtra("email"))




        FirebaseMessaging.getInstance().token.addOnCompleteListener {

            if(it.isSuccessful)
                Log.e("notif id", it.result.toString())
        }



        firebaseAuth = Firebase.auth
        val user = firebaseAuth.currentUser

        val uid = user!!.uid
        val email = user?.email
        val name = user?.displayName

        Log.e("userInfo","${uid} ${name} ${email}")

        database = FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

        dbRef = database.getReference("Users")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(uid))
                {
                    val user = dataSnapshot.child(uid).getValue(User::class.java)
                    if(user!!.notificationId == null)
                    {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener {
                            if(it.isSuccessful)
                            {
                                user.notificationId =  it.result
                                dbRef.child(uid).setValue(user)
                            }
                        }


                    }

                }
                else
                {
                    FirebaseMessaging.getInstance().token.addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            val notif = it.result
                            val user = User(uid,name,email,notif)
                            dbRef.child(uid).setValue(user)
                        }
                    }
//                    val user = User(uid,name,email)
//                    dbRef.child(uid).setValue(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("userserror", "loadItems:onCancelled", error.toException())
            }
        })




        /// navigation

        val navController = this.findNavController(R.id.myNavHostFragment)


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.myItemsFragment, R.id.shoppingCartFragment,R.id.profileFragment
            ), drawerLayout
        )



        NavigationUI.setupWithNavController(binding.bottomNavigationView,navController)

        binding.bottomNavigationView.setOnItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, navController)

            return@setOnItemSelectedListener true
        }


    }
}