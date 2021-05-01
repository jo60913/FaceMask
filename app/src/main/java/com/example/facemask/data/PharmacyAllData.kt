package com.example.facemask.data

object PharmacyAllData {
    private lateinit var pharmacy: PharmacyInfo
    fun setAllData(data:PharmacyInfo){
        this.pharmacy = data
    }

    fun getAllDatat():PharmacyInfo{
        return pharmacy
    }
}