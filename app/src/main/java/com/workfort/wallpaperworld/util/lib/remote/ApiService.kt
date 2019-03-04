package com.workfort.wallpaperworld.util.lib.remote

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiService {
    companion object {
        fun create(): ApiClient {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                //.baseUrl("http://agramonia.com/ww/")
                .baseUrl("http://192.168.2.53/ww/")
                .build()

            return retrofit.create(ApiClient::class.java)
        }
    }
}