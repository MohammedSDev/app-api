package com.digital.app.api

import com.digital.app.ErrorResponseModel
import com.digital.app.LoginModel
import com.digital.app.RetrofitObject

fun test(){
    val p = hashMapOf<String,Any>()
    RetrofitObject.retrofit
    post("users")
        .preRequest {
            queryParam = p
            delay = 2000L
        }
        .onSuccess {  }
        .onError {  }
        .call<LoginModel,ErrorResponseModel>()


}


fun post(endPoint:String): AppFunctions {
    val appReq = AppRequest(endPoint)

    return AppFunctions(AppMethod.POST,appReq)

}

fun put(endPoint:String): AppFunctions {
    val appReq = AppRequest(endPoint)

    return AppFunctions(AppMethod.PUT,appReq)

}

fun get(endPoint:String): AppFunctions {
    val appReq = AppRequest(endPoint)

    return AppFunctions(AppMethod.GET,appReq)

}

fun delete(endPoint:String): AppFunctions {
    val appReq = AppRequest(endPoint)

    return AppFunctions(AppMethod.DELETE,appReq)

}



