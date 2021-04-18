package com.example.facemask.data

import com.google.gson.annotations.SerializedName

class PharmacyInfo (
    @SerializedName("type")
    val type : String,
    @SerializedName("features")
    val features:List<Feature>
)

class Feature(
    val properties : Property
)

class Property(
    val name : String,
    val phone : String
)