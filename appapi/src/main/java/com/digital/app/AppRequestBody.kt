package com.digital.app

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink

class AppRequestBody(val requestBody:RequestBody)  {

    companion object{
        @JvmStatic
        fun create(form: MediaType, value: String):AppRequestBody{
            val r = RequestBody.create(form,value)
            return AppRequestBody(r)
        }
    }





}