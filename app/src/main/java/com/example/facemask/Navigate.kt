package com.example.facemask

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import com.example.facemask.data.CountyUtil
import com.example.facemask.data.PharmacyAllData
import com.example.facemask.data.PharmacyInfo
import com.example.facemask.databinding.ActivityMapBinding
import com.example.facemask.databinding.ActivityNavigateBinding
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class Navigate : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener {
    private var locationPermissionGrand = false
    private var googleMap: GoogleMap? = null
    private lateinit var mLocationProviderClient: FusedLocationProviderClient
    private lateinit var Binding: ActivityNavigateBinding
    private var userDistance : Int = 1000
    private var pharmacyInfo: PharmacyInfo? = null
    private var currentLocation:LatLng? = null
    private var touchSreen : Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Binding = ActivityNavigateBinding.inflate(layoutInflater)
        setContentView(Binding.root)
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        init()
        pharmacyInfo = PharmacyAllData.getAllDatat()
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    /**
     * 初始化選單並自動把畫面跳到該藥局上
     */
    private fun init(){
        val countryAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item, CountyUtil.getDistance())
        Binding.spinnerMapDistance.adapter = countryAdapter
        Binding.spinnerMapDistance.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(Binding.spinnerMapDistance.selectedItem.toString() == "1公里距離")
                    userDistance = 1000
                else if(Binding.spinnerMapDistance.selectedItem.toString() == "3公里距離")
                    userDistance = 3000
                touchSreen = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }



    }


    private fun getLocationPermission() {
        //這邊只是確認是否有授權過 沒有就會跑requestLocaitonPermission()
        if(ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"獲取位置權限，可以開始獲取經緯度", Toast.LENGTH_SHORT).show()
            locationPermissionGrand = true
            checkGPSState()
        }else{  //如果沒有獲取權限就會跑這行，詢問獲取權限
            requestLocationPermission()
        }
    }
    private fun checkGPSState(){
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {   //來確認是否有開啟GPS服務
            Toast.makeText(this,"已獲得權限並開啟GPS，開始獲取經緯度", Toast.LENGTH_SHORT).show()
            getDeviceLocation()
        }else{  //如果沒有開啟的話
            AlertDialog.Builder(this)
                    .setTitle("GPS尚未開啟")
                    .setMessage("請開啟GPS方便定位")
                    .setPositiveButton("確定"){_,_ ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivityForResult(intent, REQUEST_GPS_STATE)
                    }.setNegativeButton("取消",null)
                    .show()
        }
    }

    private fun getDeviceLocation() {
        try{
            if(locationPermissionGrand == true){
                val locationRequest = LocationRequest()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                locationRequest.interval = 1000 //Unit microsecond

                mLocationProviderClient.requestLocationUpdates(locationRequest,object : LocationCallback(){
                    override fun onLocationResult(locationRequest: LocationResult?) {
                        locationRequest ?: return
                        Log.d("MainActivity","緯度${locationRequest.lastLocation.altitude}， 精度 ${locationRequest.lastLocation.longitude}")

                        currentLocation = LatLng(locationRequest.lastLocation.latitude,locationRequest.lastLocation.longitude)
                        Log.d("getDevice","${locationRequest.lastLocation.latitude},${locationRequest.lastLocation.longitude}")
                        val camerZoom = when(userDistance) {
                            1000 -> 15.0f
                            3000 -> 13.3f
                            else -> 13.3f
                        }



                        googleMap?.isMyLocationEnabled = true
                        if(touchSreen) {        //讓畫面只刷新一次
                            googleMap?.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(currentLocation, camerZoom)
                            )
                            touchSreen = false
                        }

                        val pharmacyLocation = Location("pharmacy")
                        val userLocation = Location("User")
                        userLocation.latitude = locationRequest.lastLocation.latitude
                        userLocation.longitude = locationRequest.lastLocation.longitude
                        val filter = pharmacyInfo?.features
                        googleMap?.clear()
                        filter?.forEach {
                            pharmacyLocation.latitude = it.geometry.coordinates.get(1)
                            pharmacyLocation.longitude = it.geometry.coordinates.get(0)
                            if(userLocation.distanceTo(pharmacyLocation) < userDistance){
                                googleMap?.addMarker(
                                        MarkerOptions()
                                                .position(LatLng(
                                                        it.geometry.coordinates.get(1),
                                                        it.geometry.coordinates.get(0)
                                                ))
                                                .title(it.properties.name)
                                                .snippet("${it.properties.mask_adult},"+ "${it.properties.mask_child}")
                                )
                            }
                        }
                    }
                },null)
            }else{
                getLocationPermission()
            }
        }catch(e:SecurityException){
            e.printStackTrace()
        }
    }

    /**
     * 在授權的頁面AlertDialog按了甚麼都會回來這裡做處裡
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_lOCATION_PERMISSION ->{
                if(grantResults.isNotEmpty()){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        locationPermissionGrand = true
                        checkGPSState()
                    }else if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){//如果拒絕的話
                            Toast.makeText(this,"位置權限被關閉，無法正常使用", Toast.LENGTH_SHORT).show()
                            AlertDialog.Builder(this)
                                    .setTitle("權限拒絕")
                                    .setMessage("需要開啟權限才可以可正常使用")
                                    .setPositiveButton("正確",){_,_ ->
                                        val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                                        startActivityForResult(intent,REQUEST_lOCATION_PERMISSION)
                                    }.setPositiveButton("取消"){_,_->
                                        requestLocationPermission()
                                    }.show()
                        }else{
                            Toast.makeText(this,"權限被拒絕，功能無法使用", Toast.LENGTH_SHORT).show()
                            requestLocationPermission()
                        }
                    }
                }
            }
        }
    }

    /**
     * 如果跑去設定那麼畫面再回來就會來這裡
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_lOCATION_PERMISSION ->{
                getLocationPermission()
            }

            REQUEST_GPS_STATE ->{
                checkGPSState()
            }
        }
    }

    private fun requestLocationPermission() {
        //如果曾經按下不再顯示或已經授權這邊就不會再顯示任何東西了(shouldShowRequestPermissionRationale == false)，
        // 如果之前指按下每次使用時授權那麼每次都會出現詢問視窗 (shouldShowRequestPermissionRationale = true)
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)){
            AlertDialog.Builder(this)
                    .setMessage("應用程式需要獲取位置權限")
                    .setPositiveButton("確定") { _, _ ->
                        ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_lOCATION_PERMISSION)
                    }
                    .setNegativeButton("取消"){_,_ -> requestLocationPermission()}
                    .show()
        }else{//可以要求詢問權限

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_lOCATION_PERMISSION)
        }
    }

    override fun onMapReady(googlemap: GoogleMap?) {
        googleMap = googlemap
        googleMap?.setInfoWindowAdapter(InfoWindowAdapter(this@Navigate))
        googlemap?.setOnInfoWindowClickListener(this)
        checkGPSState()
        Log.d("onItem",currentLocation.toString())

    }





    override fun onInfoWindowClick(marker: Marker?) {
        val name:String = marker!!.title
        val snippet = marker!!.snippet
        Log.d("onInfo",snippet.toString())

        marker!!.title.let {
            val filterData =
                    pharmacyInfo?.features?.filter {
                        it.properties.name == name &&
                                "${it.properties.mask_adult},${it.properties.mask_child}" == snippet
                    }
            if(filterData?.size!! > 0){
                val intent = Intent(this,PharmacyDetailActivity::class.java)
                intent.putExtra("data",filterData.get(0))
                startActivity(intent)
            }else{
                Toast.makeText(this,"找不到資料", Toast.LENGTH_SHORT).show()
            }
        }
    }

}