package com.example.facemask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import com.example.facemask.data.PharmacyInfo
import com.example.facemask.databinding.ActivityMainBinding
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var Binding : ActivityMainBinding
    val url = "https://raw.githubusercontent.com/thishkt/pharmacies/master/data/info.json"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(Binding.root)

        Binding.progressBar.visibility = View.VISIBLE
        okhttpUtil.getAsync(PHAMACY_URL, object : okhttpUtil.ICallBack{
            override fun onResponse(response: Response) {
                val respon = response.body?.string()

                val pharmacyInfo = Gson().fromJson(respon,PharmacyInfo::class.java)
                val builder : StringBuilder = java.lang.StringBuilder()
                for(i in pharmacyInfo.features){
                    builder.append("藥局名稱 ${i.properties.name} , 藥局電話 ${i.properties.phone}"+"\n")
                    //Log.d("onresponse","藥局名稱 ${i.properties.name} , 藥局電話 ${i.properties.phone}")
                }
                runOnUiThread {
                    Binding.showtext.text = builder.toString()
                    Binding.progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(e: okio.IOException) {
                Log.e("faceMask","call enqueue $e")
                runOnUiThread {
                    Binding.progressBar.visibility = View.GONE
                }
            }
        })
    }

//    private fun getPharmaciesData(){
//        Binding.progressBar.visibility = View.VISIBLE
//        val PharmaciesData = "https://raw.githubusercontent.com/thishkt/pharmacies/master/data/info.json"
//        val okHttpClient = OkHttpClient().newBuilder().build();
//        val request = Request.Builder().url(PharmaciesData).get().build()
//        val call = okHttpClient.newCall(request)
//        call.enqueue(object : Callback{
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e("faceMask","call enqueue $e")
//                runOnUiThread {
//                    Binding.progressBar.visibility = View.GONE
//                }
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val respon = response.body?.string()
//
//                val pharmacyInfo = Gson().fromJson(respon,PharmacyInfo::class.java)
//                val builder : StringBuilder = java.lang.StringBuilder()
//                for(i in pharmacyInfo.features){
//                    builder.append("藥局名稱 ${i.properties.name} , 藥局電話 ${i.properties.phone}"+"\n")
//                    //Log.d("onresponse","藥局名稱 ${i.properties.name} , 藥局電話 ${i.properties.phone}")
//                }
//                runOnUiThread {
//                    Binding.showtext.text = builder.toString()
//                    Binding.progressBar.visibility = View.GONE
//                }
////                Log.d("onresponse","type : ${pharmacyInfo.type}")
////                val obj = JSONObject(respon)
////                val featureArray = JSONArray(obj.getString("features"))
////                val propertiesName : StringBuilder = java.lang.StringBuilder()
////                for(i in 0 until  featureArray.length()){
//////                    if(!obj.isNull("prperties")){
//////                        如果不為空就跑這行，也可以用try catch 來捕捉
//////                    }
////                    if(featureArray.getJSONObject(i).has("properties")){
////                        val properties = featureArray.getJSONObject(i).getString("properties")
////                        val property =JSONObject(properties)
////                        propertiesName.append(property.getString("name")+"\n")
////                    }else{
////                        Log.e("MainActivity onResponse","沒有properties這個值")
////                    }
////                }
////                Log.d("onresponse","藥局名稱 ${propertiesName.toString()}")
//
//            }
//        })
//    }
}