package com.example.resellapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.resellapp.databinding.ActivityNavigationBinding

class NavigationActivity: AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        drawerLayout = binding.drawerLayout

        setContentView(binding.root)


        val bundle = Bundle()
        bundle.putString("email", intent.getStringExtra("email"))


        val navController = this.findNavController(R.id.myNavHostFragment)

//        navController.navigate(R.id.profileFragment,bundle)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.myItemsFragment, R.id.shoppingCartFragment,R.id.profileFragment
            ), drawerLayout
        )


//        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration)

        NavigationUI.setupWithNavController(binding.bottomNavigationView,navController)

    }
}