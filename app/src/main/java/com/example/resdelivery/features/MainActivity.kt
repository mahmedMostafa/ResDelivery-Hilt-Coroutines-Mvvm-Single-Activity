package com.example.resdelivery.features

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.resdelivery.R
import com.example.resdelivery.databinding.ActivityMainBinding
import com.example.resdelivery.util.NetworkUtils
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)// Set AppTheme before setting content view.
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        checkForInternetConnection()
    }

    // we observed the internet connection here since our main activity contains all of our fragments
    // and we want to get notified across the whole app
    private fun checkForInternetConnection() {
        val snackbar =
            Snackbar.make(
                binding.mainContent,
                R.string.no_internet_connection,
                Snackbar.LENGTH_INDEFINITE
            )
        NetworkUtils.getNetworkLiveData(this).observe(this, Observer {
            if (it) {
                snackbar.dismiss()
            } else {
                snackbar.show()
            }
        }
        )
    }

}
