package com.digital.appapidemo

import android.app.Application
import com.digital.app.config.appConfig

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appConfig {
            CONNECT_ADDRESS_ERROR_MESSAGE = ""
//            OK_HTTP_CLIENT = null
//            errorModel = MainErrorModel::class.java

        }
    }
}