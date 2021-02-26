@file:Suppress("DEPRECATION")

package com.example.restaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant.api.Network
import com.example.restaurant.auth.LoginActivity

import com.example.restaurant.databinding.ActivitySplashScreenBinding
import com.example.restaurant.home.HomeActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var api:Network
    lateinit var binding:ActivitySplashScreenBinding
    var errorCode=9001

    private lateinit var response1: Response<ResponseBody>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)

        intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)


    }












}


