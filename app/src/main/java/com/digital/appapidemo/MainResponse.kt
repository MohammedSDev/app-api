package com.digital.appapidemo

import com.digital.app.ResponseModel

open class MainResponse(open val success:Boolean) : ResponseModel {
    private var c = 0
    override var code: Int
        get() = c
        set(value) {
            c = value
        }
}
data class SecondResponse(override val success:Boolean) : MainResponse(success) {
//        get() = field ?: false
//    @SerializedName("message", alternate = arrayOf("error"))
//    var message: String? = null
//    var messageRes: Int? = null
//    val data: T? = null
//    @SerializedName("status_code")
//    var statusCode: Int? = null
}