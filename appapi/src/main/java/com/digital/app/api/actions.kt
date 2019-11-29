package com.digital.app.api

import com.digital.app.ErrorResponseModel
import com.digital.app.ResponseModel
import com.digital.app.config.Constants
import io.reactivex.internal.util.ErrorMode
import java.io.File

fun <T : ResponseModel, E : ErrorResponseModel> post(
    endPoint: String,
    responseModel: Class<T>,
    errorMode: Class<E>
)
        : AppFunctions<T, E> {
    val appReq = AppRequestParam(endPoint)


//    return
//    Constants.customAppFunction?.getConstructor(AppMethod::class.java,
//        AppRequestParam::class.java)?.newInstance(
//        AppMethod.POST, appReq)
//        ?:
    return AppFunctions<T, E>(AppMethod.POST, appReq).also {
        it.responseModel = responseModel
        it.errorModel = errorMode
    }
}


fun <T : ResponseModel, E : ErrorResponseModel> put(
    endPoint: String,
    responseModel: Class<T>,
    errorMode: Class<E>
): AppFunctions<T, E> {
    val appReq = AppRequestParam(endPoint)

//    return Constants.customAppFunction?.getConstructor(AppMethod::class.java,
//        AppRequestParam::class.java)?.newInstance(
//        AppMethod.PUT, appReq)
//        ?:
    return AppFunctions<T, E>(AppMethod.PUT, appReq).also {
        it.responseModel = responseModel
        it.errorModel = errorMode
    }


}

fun <T : ResponseModel, E : ErrorResponseModel> get(
    endPoint: String,
    responseModel: Class<T>,
    errorMode: Class<E>
): AppFunctions<T, E> {
    val appReq = AppRequestParam(endPoint)


//    return Constants.customAppFunction?.getConstructor(AppMethod::class.java,
//        AppRequestParam::class.java)?.newInstance(
//        AppMethod.GET, appReq)
//        ?:
    return AppFunctions<T, E>(AppMethod.GET, appReq).also {
        it.responseModel = responseModel
        it.errorModel = errorMode
    }
}

fun <T : ResponseModel, E : ErrorResponseModel> delete(
    endPoint: String,
    responseModel: Class<T>,
    errorMode: Class<E>
): AppFunctions<T, E> {
    val appReq = AppRequestParam(endPoint)

//    return Constants.customAppFunction?.getConstructor(AppMethod::class.java,
//        AppRequestParam::class.java)?.newInstance(
//        AppMethod.DELETE, appReq)
//        ?:
    return AppFunctions<T, E>(AppMethod.DELETE, appReq).also {
        it.responseModel = responseModel
        it.errorModel = errorMode
    }
}


fun <T : ResponseModel, E : ErrorResponseModel, A : AppFunctions<T, E>> post(
    customAppFunction: Class<A>,
    responseModel: Class<T>,
    errorMode: Class<E>
): A {
    val appReq = AppRequestParam("")

//    val instance = A::class.java.getDeclaredConstructor(A::class.java)
//        .newInstance()
    val instance2 =
        customAppFunction.getConstructor(AppMethod::class.java, AppRequestParam::class.java)
            .newInstance(AppMethod.POST, appReq)
    instance2.responseModel = responseModel
    instance2.errorModel = errorMode

    return instance2
}

fun <T : ResponseModel, E : ErrorResponseModel, A : AppFunctions<T, E>> get(
    customAppFunction: Class<A>,
    responseModel: Class<T>,
    errorMode: Class<E>
): A {
    val appReq = AppRequestParam("")
    val instance2 = customAppFunction.getConstructor(AppMethod::class.java, AppRequestParam::class.java)
        .newInstance(AppMethod.GET, appReq)
    instance2.responseModel = responseModel
    instance2.errorModel = errorMode

    return instance2
}

fun <T : ResponseModel, E : ErrorResponseModel, A : AppFunctions<T, E>> put(
    customAppFunction: Class<A>,
    responseModel: Class<T>,
    errorMode: Class<E>
): A {
    val appReq = AppRequestParam("")
    val instance2 = customAppFunction.getConstructor(AppMethod::class.java, AppRequestParam::class.java)
        .newInstance(AppMethod.PUT, appReq)
    instance2.responseModel = responseModel
    instance2.errorModel = errorMode

    return instance2
}

fun <T : ResponseModel, E : ErrorResponseModel, A : AppFunctions<T, E>> delete(
    customAppFunction: Class<A>,
    responseModel: Class<T>,
    errorMode: Class<E>
): A {
    val appReq = AppRequestParam("")
    val instance2 = customAppFunction.getConstructor(AppMethod::class.java, AppRequestParam::class.java)
        .newInstance(AppMethod.DELETE, appReq)
    instance2.responseModel = responseModel
    instance2.errorModel = errorMode

    return instance2
}


fun download(
    file: File,
    appRequestParam: AppRequestParam,
    onSuccess: (r: ResponseModel) -> Unit,
    onError: (r: ErrorResponseModel) -> Unit
) {

    AppFunctions<ResponseModel,ErrorResponseModel>(AppMethod.GET, appRequestParam)
        .onSuccess(onSuccess)
        .onError(onError)
        .download(file)
}