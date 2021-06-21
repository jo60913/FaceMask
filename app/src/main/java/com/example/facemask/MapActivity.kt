package com.example.facemask

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.example.facemask.Util.OkHttpUtil
import com.example.facemask.data.CountyUtil
import com.example.facemask.data.Feature
import com.example.facemask.data.PharmacyAllData
import com.example.facemask.data.PharmacyInfo
import com.example.facemask.databinding.ActivityMapBinding
import okhttp3.Response
import okio.IOException

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private var locationPermissionGrand = false
    private var googleMap: GoogleMap? = null
    private lateinit var mLocationProviderClient: FusedLocationProviderClient
    private lateinit var Binding: ActivityMapBinding
    private var currentCountryName: String = "臺南市"
    private var currentTownName: String = "永康區"
    private var pharmacyInfo: PharmacyInfo? = null
    private var currentLocation: LatLng? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(Binding.root)
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        init()
        pharmacyInfo = PharmacyAllData.getAllDatat()
        updateMark()
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getDeviceLocation()
    }


    /**
     * 初始化選單並自動把畫面跳到該藥局上
     */
    private fun init() {
        val countryAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, CountyUtil.getAllCountiesName())
        Binding.spinnerMapCountry.adapter = countryAdapter
        Binding.spinnerMapCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentCountryName = Binding.spinnerMapCountry.selectedItem.toString()
                setSpinnerTown()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        Binding.spinnerMapTown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentTownName = Binding.spinnerMapTown.selectedItem.toString()
                Log.d("init", "城市$currentCountryName 鄉鎮 $currentTownName")
                updateMark()
                val filter = pharmacyInfo?.features?.filter {
                    it.properties.county == currentCountryName && it.properties.town == currentTownName
                }
                var location: LatLng? = LatLng(0.0, 0.0)
                if (filter?.isNotEmpty()!!) {
                    filter?.get(0)?.geometry?.coordinates.let {
                        if (it != null) {
                            location = LatLng(it.get(1), it.get(0))
                        }
                    }
                }
                googleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(location, 15f)
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        setDefaultCountry()
    }

    private fun setDefaultCountry() {
        Binding.spinnerMapCountry.setSelection(CountyUtil.getCountyIndexByName(currentCountryName))
        setSpinnerTown()
    }






    private fun getDeviceLocation() {
        try {
            if (locationPermissionGrand == true) {
                val locationRequest = LocationRequest()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                locationRequest.interval = 1000 //Unit microsecond

                mLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationRequest: LocationResult?) {
                        locationRequest ?: return
                        Log.d("MainActivity", "緯度${locationRequest.lastLocation.altitude}， 精度 ${locationRequest.lastLocation.longitude}")

                        currentLocation = LatLng(locationRequest.lastLocation.latitude, locationRequest.lastLocation.longitude)
                        Log.d("getDevice", "${locationRequest.lastLocation.latitude},${locationRequest.lastLocation.longitude}")


                        googleMap?.isMyLocationEnabled = true

                    }
                }, null)
            } else {

            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }







    override fun onMapReady(googlemap: GoogleMap?) {
        googleMap = googlemap
        googleMap?.setInfoWindowAdapter(InfoWindowAdapter(this@MapActivity))
        googlemap?.setOnInfoWindowClickListener(this)

        Log.d("onItem", currentLocation.toString())


    }

    private fun setSpinnerTown() {
        val Adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, CountyUtil.getTownsByCountyName(currentCountryName))
        Binding.spinnerMapTown.adapter = Adapter
        Binding.spinnerMapTown.setSelection(CountyUtil.getTownIndexByName(currentCountryName, currentTownName))
    }

    private fun updateMark() {
        val filter = pharmacyInfo?.features?.filter {
            it.properties.county == currentCountryName && it.properties.town == currentTownName
        }
        googleMap?.clear()
        filter?.forEach {
            googleMap?.addMarker(
                    MarkerOptions()
                            .position(LatLng(
                                    it.geometry.coordinates.get(1),
                                    it.geometry.coordinates.get(0)
                            ))
                            .title(it.properties.name)
                            .snippet("${it.properties.mask_adult}," + "${it.properties.mask_child}")
            )
        }

    }

    override fun onInfoWindowClick(marker: Marker?) {
        val name: String = marker!!.title
        val snippet = marker!!.snippet
        Log.d("onInfo", snippet.toString())
        marker!!.title.let {
            val filterData =
                    pharmacyInfo?.features?.filter {
                        it.properties.name == name &&
                                "${it.properties.mask_adult},${it.properties.mask_child}" == snippet
                    }
            if (filterData?.size!! > 0) {
                val intent = Intent(this, PharmacyDetailActivity::class.java)
                intent.putExtra("data", filterData.get(0))
                startActivity(intent)
            } else {
                Toast.makeText(this, "找不到資料", Toast.LENGTH_SHORT).show()
            }
        }
    }
}