package com.digital.appapidemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.digital.app.*
import com.digital.app.api.createRequestPart
import com.digital.app.api.getMimeType
import com.digital.app.api.post
import com.digital.app.config.AppUploadableFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import kotlin.reflect.KFunction0

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }


    fun multiPartTest(){



        post("users")
            .preRequest {
                isMultiPart = true
                multiBodyParam = hashMapOf("name" to createRequestPart("mohammed"))
                multiPartFiles = listOf(AppUploadableFile("fName"
                    , File.createTempFile("","")
                , getMimeType("")?:"*/*"))
            }
            .call<MainResponse,MainErrorModel>()
    }


}
