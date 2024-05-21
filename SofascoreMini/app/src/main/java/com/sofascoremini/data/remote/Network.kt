package com.sofascoremini.data.remote


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration


const val BASE_URL = "https://academy-backend.sofascore.dev/"

object Network {

    private var INSTANCE: ApiService? = null

    private val okHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(10.seconds.toJavaDuration()).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()


    fun getInstance(): ApiService {
        if (INSTANCE == null) {
            INSTANCE = retrofit
                .create(ApiService::class.java)
        }
        return INSTANCE!!
    }


}