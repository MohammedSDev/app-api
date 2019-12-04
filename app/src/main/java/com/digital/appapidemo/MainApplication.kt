package com.digital.appapidemo

import android.app.Application
import com.digital.app.config.appConfig
import java.util.*

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appConfig {
            //BASE_URL should be end with `/`
            BASE_URL = "https://github.com/MohammedSDev/"
            //you can provide generic error message based on your user locale
            GENERAL_ERROR_MESSAGE = if(Locale.getDefault().language.equals("ar"))
                "حدث خطأ ماء, الرجاء المحاولة لاحقاً"
            else
                "Some thing went wrong,tray later"
        }
    }
}