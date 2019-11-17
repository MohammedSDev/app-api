package com.digital.app

sealed class AppNetworkStatus {
    data class InProgress(val message:String? = null,var tag:Any? = null) : AppNetworkStatus()
    data class OnSuccess(val message:String? = null,var tag:Any? = null) : AppNetworkStatus()
    data class OnError(val message:String? = null,var tag:Any? = null) : AppNetworkStatus()
    data class OnCancel(val message:String? = null,var tag:Any? = null) : AppNetworkStatus()
    data class OnCustom(val key:Int = -1,val message:String? = null,var tag:Any? = null) : AppNetworkStatus()
}