package com

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import com.example.facemask.R
import com.example.facemask.data.Feature
import com.example.facemask.databinding.ActivityPharmacyDetailBinding

class PharmacyDetailActivity : AppCompatActivity() {
    private lateinit var Binding:ActivityPharmacyDetailBinding
    private val data by lazy {
        intent.getSerializableExtra("data") as Feature  //轉型為Feature型態
    }
    private val name by lazy{data?.properties?.name}
    private val adultMaskAmout by lazy{data?.properties?.mask_adult}
    private val childMaskAmout by lazy{data?.properties?.mask_child}
    private val location by lazy{data?.properties?.address}
    private val phone by lazy{data?.properties?.phone}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Binding = ActivityPharmacyDetailBinding.inflate(layoutInflater)
        setContentView(Binding.root)
        init()
    }

    private fun init(){
        Binding.itemName.text = name ?: "資料錯誤"
        Binding.tvAdult.text = adultMaskAmout.toString()
        Binding.tvChild.text = childMaskAmout.toString()
        Binding.itemViewPhone.text = phone
        Binding.itemViewAddress.text = location
    }
}