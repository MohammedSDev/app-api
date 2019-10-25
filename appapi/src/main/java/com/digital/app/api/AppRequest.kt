package com.digital.app.api


/**
 * container of request params & options
 * */
class AppRequest(var endPoint: String){
    var headerParam: HashMap<String,String> = hashMapOf()
    var queryParam: HashMap<String,Any> = hashMapOf()
    var bodyParam: HashMap<String,Any> = hashMapOf()
    var delay: Long = 0//ms
    var observeOnMainThread: Boolean? = null//ms
}