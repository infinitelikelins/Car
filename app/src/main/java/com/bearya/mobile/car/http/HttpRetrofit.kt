package com.bearya.mobile.car.http

import com.bearya.mobile.car.BuildConfig
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class HttpRetrofit protected constructor() {

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

        val cookieJar : CookieJar = object : CookieJar {
            private val cookieStore : HashMap<String, List<Cookie>> = HashMap()
            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return cookieStore[url.host] ?: emptyList()
            }
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookieStore[url.host] = cookies
            }
        }

        OkHttpClient.Builder()
                .connectTimeout(5 * 1000L, TimeUnit.MILLISECONDS)
                .readTimeout(5 * 1000L, TimeUnit.MILLISECONDS)
                .callTimeout(5 * 1000L, TimeUnit.MILLISECONDS)
                .addInterceptor(loggingInterceptor)
                .cookieJar(cookieJar)
                .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.ApiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}