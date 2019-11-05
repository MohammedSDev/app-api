package com.digital.app.config

import com.digital.app.ResponseModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


 object Constants {
    var OK_HTTP_CLIENT:OkHttpClient.Builder? = null
    var OK_HTTP_CLIENT_KEEP_PURE:Boolean = false
    var BASE_URL = ""
    var TIMEOUT_UNIT = TimeUnit.SECONDS
    var CONNECT_TIMEOUT:Long = 20//TimeUnit.SECONDS
    var READ_TIMEOUT:Long = 20//TimeUnit.SECONDS
    var WRITE_TIMEOUT:Long = 30//TimeUnit.SECONDS
    var DEBUG_LEVEL:HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
    var OBSERVER_ON_MAIN_THREAD:Boolean = true
    var ADAPTERS:List<AppApiAdapterComponent> = listOf()






    const val CONNECT_ERROR_CODE = 3021
    const val CONNECT_TIME_OUT_ERROR_CODE = 3022
    const val CONNECT_ADDRESS_ERROR_CODE = 3023
    const val RESPONSE_EMPTY_ERROR_CODE = 3032
    const val ERROR_RESPONSE_EMPTY_ERROR_CODE = 3033


    var GENERAL_ERROR_MESSAGE = "Some thing went wrong. try later"
    var CONNECT_ERROR_MESSAGE = "Check your connection."
    var CONNECT_TIME_OUT_ERROR_MESSAGE = "Time out, try later."
    var CONNECT_ADDRESS_ERROR_MESSAGE = "Host address could not be determined."

}