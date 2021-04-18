package com.example.facemask

import okhttp3.*
import okio.IOException

object okhttpUtil {
    private lateinit var okHttpClient : OkHttpClient
    init{
        okHttpClient = OkHttpClient().newBuilder().build();
    }

    /**
     * url 要請求的網路
     * callback要實作回調的對象。實際是okhttp.callback會直接呼叫這個callback的實作方法
     */
    fun getAsync(url:String,callback:ICallBack){
        val request = with(Request.Builder()){
            url(url)
            get()
            build()
        }

        val call = okHttpClient?.newCall((request))

        call?.enqueue(object : Callback{
            override fun onFailure(call: Call, e: java.io.IOException) {
                callback.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onResponse(response)
            }
        })
    }

    interface ICallBack{
        fun onResponse(response: Response)
        fun onFailure(e:IOException)
    }
}

