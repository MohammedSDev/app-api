package com.digital.appapidemo

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET


interface MyApi{


    @GET("/")
    fun testGet():Call<Response<Any>>
}