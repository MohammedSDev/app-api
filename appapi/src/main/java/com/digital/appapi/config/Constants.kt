package com.digital.appapi.config

import okhttp3.logging.HttpLoggingInterceptor
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

 object Constants {
    var BASE_URL = ""
    var TIMEOUT_UNIT = TimeUnit.SECONDS
    var CONNECT_TIMEOUT:Long = 20//TimeUnit.SECONDS
    var READ_TIMEOUT:Long = 20//TimeUnit.SECONDS
    var WRITE_TIMEOUT:Long = 30//TimeUnit.SECONDS
    var DEBUG_LEVEL:HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
    var ADAPTERS:List<AppApiAdapterComponent> = listOf()
//    var ApiService:Class<out ApiInterface> = ApiInterface::class.java



    const val NONE = 0
    const val BASIC = 1
    const val HEADERS = 2
    const val BODY = 3

}