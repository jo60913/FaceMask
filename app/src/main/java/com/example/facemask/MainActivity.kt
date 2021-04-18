package com.example.facemask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPharmaciesData()
    }

    private fun getPharmaciesData(){
        val PharmaciesData = "https://raw.githubusercontent.com/thishkt/pharmacies/master/data/info.json"
        val okHttpClient = OkHttpClient().newBuilder().build();
        val request = Request.Builder().url(PharmaciesData).get().build()
        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("faceMask","call enqueue $e")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("faceMask","${response.body?.string()}")
            }
        })

    }
}