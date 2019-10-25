package com.digital.app.api

import com.digital.app.ErrorResponseModel
import com.digital.app.ResponseModel
import java.io.File

fun post(endPoint: String): AppFunctions {
    val appReq = AppRequest(endPoint)

    return AppFunctions(AppMethod.POST, appReq)

}

fun put(endPoint: String): AppFunctions {
    val appReq = AppRequest(endPoint)

    return AppFunctions(AppMethod.PUT, appReq)

}

fun get(endPoint: String): AppFunctions {
    val appReq = AppRequest(endPoint)

    return AppFunctions(AppMethod.GET, appReq)

}

fun delete(endPoint: String): AppFunctions {
    val appReq = AppRequest(endPoint)

    return AppFunctions(AppMethod.DELETE, appReq)

}


inline fun <reified A : AppFunctions> post(): A {
    val appReq = AppRequest("")

//    val instance = A::class.java.getDeclaredConstructor(A::class.java)
//        .newInstance()
    val instance2 = A::class.java.getConstructor(AppMethod::class.java, AppRequest::class.java)
        .newInstance(AppMethod.POST, appReq)
    return instance2
}

inline fun <reified A : AppFunctions> get(): A {
    val appReq = AppRequest("")
    val instance2 = A::class.java.getConstructor(AppMethod::class.java, AppRequest::class.java)
        .newInstance(AppMethod.GET, appReq)
    return instance2
}

inline fun <reified A : AppFunctions> put(): A {
    val appReq = AppRequest("")
    val instance2 = A::class.java.getConstructor(AppMethod::class.java, AppRequest::class.java)
        .newInstance(AppMethod.PUT, appReq)
    return instance2
}

inline fun <reified A : AppFunctions> delete(): A {
    val appReq = AppRequest("")
    val instance2 = A::class.java.getConstructor(AppMethod::class.java, AppRequest::class.java)
        .newInstance(AppMethod.DELETE, appReq)
    return instance2
}


fun download(
    file: File,
    appRequest: AppRequest,
    onSuccess: (r: ResponseModel) -> Unit,
    onError: (r: ErrorResponseModel) -> Unit
) {

    AppFunctions(AppMethod.GET, appRequest)
        .onSuccess(onSuccess)
        .onError(onError)
        .download(file)
}