package com.example.resdelivery.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.resdelivery.R
import com.example.resdelivery.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(){

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

//        val abc =  AppBarConfiguration.Builder().build()
//        NavigationUI.setupActionBarWithNavController(this, binding.hostFragment.getNavController(), abc)
//        NavigationUI.setUp



//        val navHostFragment = host_fragment as NavHostFragment
//        val inflater = navHostFragment.navController.navInflater
//        val graph = inflater.inflate(R.navigation.nav_graph)
//       // graph.setDefaultArguments(intent.extras)
//        graph.startDestination = R.id.foodListFragment

//        supportFragmentManager.beginTransaction().replace(
//            R.id.container,
//            RegisterFragment()
//        ).commit()

    }


}
