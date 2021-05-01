package com.example.facemask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.example.facemask.data.PharmacyAllData
import com.example.facemask.data.PharmacyInfo
import com.example.facemask.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var Binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(Binding.root)
        Binding.navLayout.setOnClickListener {
            val intent = Intent(this,MapActivity::class.java)
            intent.putExtra("GPSState",true)
            startActivity(intent)
        }

        Binding.placeLayout.setOnClickListener {
            val intent = Intent(this,MapActivity::class.java)
            intent.putExtra("GPSState",false)
            startActivity(intent)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyUp(keyCode, event)
    }
}