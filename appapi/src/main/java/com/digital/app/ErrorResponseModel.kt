package com.digital.app

open class ErrorResponseModel : ResponseModel(){
    @Transient var errorMessage:String = ""
    @Transient var errorCode:Int = -1
}