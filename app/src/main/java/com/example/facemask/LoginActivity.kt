package com.example.facemask

import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.facemask.R
import com.example.facemask.data.PharmacyInfo
import com.google.gson.Gson
import com.example.facemask.Util.OkHttpUtil
import com.example.facemask.data.PharmacyAllData
import com.example.facemask.databinding.ActivityLoginBinding
import okhttp3.Response
import okio.IOException
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {
    private lateinit var Binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(Binding.root)
        supportActionBar?.hide()
        checkNetworkState()
        getPharmacyData()

    }

    private fun checkNetworkState(){
        val connManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetwork
        if(networkInfo == null ){
            AlertDialog.Builder(this)
                .setTitle("沒有網路")
                .setMessage("請開啟網路")
                .setPositiveButton("重試"){_,_->
                    checkNetworkState()
                    getPharmacyData()
                }.setNegativeButton("退出"){_,_->
                     finish()
                        exitProcess(0)
                    }.setCancelable(false).show()
        }
    }

    private fun getPharmacyData() {
        Binding.progressbar.visibility = View.VISIBLE
        OkHttpUtil.mOkHttpUtil.getAsync(PHARMACIES_DATA_URL, object : OkHttpUtil.ICallback{
            override fun onResponse(response: Response) {
                val respons = response.body?.string()
                Log.d("Map","pharmacy : $respons")
                val pharmacyInfo = Gson().fromJson(respons, PharmacyInfo::class.java)
                PharmacyAllData.setAllData(pharmacyInfo)
                Log.d("alldata",pharmacyInfo.toString())
                runOnUiThread {
                    Binding.progressbar.visibility =View.GONE
                    Binding.loginText.text = "載入完成"
                    val intent:Intent = Intent(this@LoginActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(e: IOException) {
                Log.d("HKT", "onFailure: $e")
                runOnUiThread {

                    Binding.progressbar.visibility =View.GONE
                }
            }

        })
    }
}