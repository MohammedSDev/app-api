package com.digital.app

sealed class AppNetworkStatus(val message: String?, var tag: Any?) {
    class InProgress(message: String? = null, tag: Any? = null) : AppNetworkStatus(message, tag)
    class OnSuccess( message: String? = null,  tag: Any? = null) : AppNetworkStatus(
        message,
        tag
    )

     class OnError( message: String? = null,  tag: Any? = null) : AppNetworkStatus(
        message,
        tag
    )

     class OnCancel( message: String? = null,  tag: Any? = null) : AppNetworkStatus(
        message,
        tag
    )

     class OnCustom( key: Int = -1,  message: String? = null,  tag: Any? = null) :
        AppNetworkStatus(
            message,
            tag
        )


//     message:String? = null
//     tag:Any? = null
}