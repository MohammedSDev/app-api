package com.digital.appapidemo

import com.digital.app.ErrorResponseModel

data class MainErrorModel(
    val success: Boolean = false,
    val error: String? = ""
) : ErrorResponseModel {
    private var c = 0
    private var mes = ""
    private var err: Throwable? = null
    override var message: String
        get() = mes
        set(value) {
            mes = value
        }
    override var throwable: Throwable?
        get() = err
        set(value) {
            err = value
        }
    override var code: Int
        get() = c
        set(value) {
            c = value
        }
}