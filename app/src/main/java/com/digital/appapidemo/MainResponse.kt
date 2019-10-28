package com.digital.appapidemo

import com.digital.app.ResponseModel

data class MainResponse(val success:Boolean) : ResponseModel() {
//        get() = field ?: false
//    @SerializedName("message", alternate = arrayOf("error"))
//    var message: String? = null
//    var messageRes: Int? = null
//    val data: T? = null
//    @SerializedName("status_code")
//    var statusCode: Int? = null
}