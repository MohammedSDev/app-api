package com.digital.appapidemo

import com.digital.app.ErrorResponseModel

data class MainErrorModel(
    val success:Boolean = false,
    val error:String?= ""
) : ErrorResponseModel()