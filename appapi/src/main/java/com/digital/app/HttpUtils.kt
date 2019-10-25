package com.digital.app


import com.digital.app.config.Constants
import okhttp3.ResponseBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import retrofit2.Response
import java.net.UnknownHostException


inline fun <reified T : ResponseModel,reified E:ErrorResponseModel> handleDataPacing2(response: retrofit2.Response<ResponseBody>): T {

    if (response.isSuccessful) {
        //success response
        val result = converter<T>(response.body())
        if (result != null)
            return result
        else {
            val text = "HeadsUp ..<>.............. new Error, body empty ................" +
                    "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                    "result: ${result}\n" +
                    "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
            appApiLog(text)
            val error = ErrorResponseModel().apply {
                errorCode = Constants.RESPONSE_EMPTY_ERROR_CODE
                errorMessage = Constants.GENERAL_ERROR_MESSAGE
            }
            throw RetrofitThrowable(error, error.errorMessage)
        }
    } else {
        throw failedResponseParcing2<E>(response)

    }
}
inline fun <reified E : ErrorResponseModel>failedResponseParcing2(response: Response<ResponseBody>): RetrofitThrowable {
    //failed response
    val error = converter<E>(response.errorBody())

    if (error != null)
        return RetrofitThrowable(error, error.errorMessage)
    else {
        val errorResponse = ErrorResponseModel().apply {
            errorCode = Constants.ERROR_RESPONSE_EMPTY_ERROR_CODE
            errorMessage = Constants.GENERAL_ERROR_MESSAGE
        }
        return RetrofitThrowable(errorResponse, errorResponse.errorMessage)
    }
}

inline fun < reified T> converter(body: ResponseBody?): T? {

    if (body == null)
        return null

    val converter = RetrofitObject
            .retrofitBodyConverter<T>(T::class.java, arrayOfNulls<Annotation>(0))
    try {
        return converter.convert(body)
    } catch (ex: IOException) {
        throw ex
        return null
    }

}

fun errorsHandling(throwable: Throwable): ErrorResponseModel {
    val error: ErrorResponseModel
    appApiLog(text = "errorsHandling, + ${throwable}")
    appApiLog(text = "errorsHandling,class simpleName + ${throwable.javaClass.simpleName}")
    appApiLog(text = "errorsHandling, message + ${throwable.message}")



    if (throwable is ConnectException) {
        error = ErrorResponseModel().apply {
            errorCode = Constants.CONNECT_ERROR_CODE
            errorMessage = Constants.CONNECT_ERROR_MESSAGE
        }
    }
    else if (throwable is SocketTimeoutException) {
        error = ErrorResponseModel().apply {
            errorCode = Constants.CONNECT_TIME_OUT_ERROR_CODE
            errorMessage = Constants.CONNECT_TIME_OUT_ERROR_MESSAGE
        }
    }
    else if (throwable is UnknownHostException) {
        error = ErrorResponseModel().apply {
            errorCode = Constants.CONNECT_ADDRESS_ERROR_CODE
            errorMessage = Constants.CONNECT_ADDRESS_ERROR_MESSAGE
        }
    }
    else if (throwable is RetrofitThrowable)
        error = throwable.error
    else {
        val text = "HeadsUp ................ new Error Not Handling................" +
                "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                "error type: ${throwable.javaClass.simpleName}\n" +
                "error message: ${throwable.message}\n" +
                "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
        appApiLog(text = text)
        error = ErrorResponseModel().apply {
            errorMessage = Constants.GENERAL_ERROR_MESSAGE
        }
    }
    return error
}

fun appApiLog(text:String,tag:String="appapi"){
//    Log.d(tag,text)
    println(text)
}
