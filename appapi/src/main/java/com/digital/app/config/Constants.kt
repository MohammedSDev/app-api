package com.digital.app.config

import com.digital.app.ResponseModel
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


 object Constants {
    var BASE_URL = "http://www.google.com"
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



    const val CONNECT_ERROR_CODE = 3021


    var GENERAL_ERROR_MESSAGE = "Some thing went wrong. try later"

}