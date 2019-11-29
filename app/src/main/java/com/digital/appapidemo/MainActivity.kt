package com.digital.appapidemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.digital.app.*
import com.digital.app.api.*
import com.digital.app.config.AppUploadableFile
import com.digital.app.config.Constants
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream
import kotlin.reflect.KFunction0

class MainActivity : AppCompatActivity() {

    class MainResponseModel : ResponseModel(){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }


    fun multiPartTest(){

        val kValue = "name" toRequest  "Mohammed"

        File("").toBase64()
        val request = post("users",MainResponseModel::class.java,MainErrorModel::class.java)
            .preRequest {
//                errorModel = MainErrorModel::class.java
                isMultiPart = true
                multiBodyParam = hashMapOf("name" to createRequestPart("mohammed"))
                multiPartFiles = listOf(AppUploadableFile("fName"
                    , File.createTempFile("","")
                , getMimeType("")?:"*/*"))
            }
            .onSuccess{it->

            }
            .onError{it->

            }
            .call()


        val cc = MainErrorModel::class.java

        AppCompositeDisposable()
//            .apply {
//            add("list_req",request)
////            get("list_req")?.cancel()
////            cancel("list_req")
//            println(get("list_req"))
//        }

    }

    val  comD =         AppCompositeDisposable()


    override fun onDestroy() {
        super.onDestroy()
//        comD.dispose()
        comD.cancelAll()
    }
}

