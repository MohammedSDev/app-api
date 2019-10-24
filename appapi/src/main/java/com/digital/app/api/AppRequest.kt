package com.digital.app.api

import com.digital.app.AppParamMap

class AppRequest(var endPoint: String){
//    var idParam: String? = null
    var headerParam: HashMap<String,String> = hashMapOf()
    var queryParam: HashMap<String,Any> = hashMapOf()
    var bodyParam: HashMap<String,Any> = hashMapOf()
    var delay: Long = 0//ms
}