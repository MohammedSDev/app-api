package com.digital.app

sealed class AppNetworkStatus {
    object InProgress : AppNetworkStatus()
    object OnSuccess : AppNetworkStatus()
    object OnError : AppNetworkStatus()
}