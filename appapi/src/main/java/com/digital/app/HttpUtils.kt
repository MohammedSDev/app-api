package com.digital.app


import com.digital.app.config.Constants
import okhttp3.ResponseBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import retrofit2.Response
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.UnknownHostException


fun <T : ResponseModel>
        handleDataPacing2(
    responseType: Class<T>,
    type: Class<out ErrorResponseModel>,
    response: Response<ResponseBody>
): T {

    if (response.isSuccessful) {
        //success response
//        val result = converter<T>(T::class.java,response.body())
        val result = converter<T>(responseType, response.body())
        if (result != null) {
            result.code = response.code()
            return result
        } else {
            val text = "HeadsUp ..<>.............. new Error, body empty ................" +
                    "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                    "result: ${result}\n" +
                    "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
            appApiLog(text)
            val error = ErrorResponseModel().apply {
                code = response.code()
                errorCode = Constants.RESPONSE_EMPTY_ERROR_CODE
                errorMessage = Constants.GENERAL_ERROR_MESSAGE
            }
            throw RetrofitThrowable(error, error.errorMessage)
        }
    } else {
        throw failedResponseParcing2(type, response)

    }
}

fun failedResponseParcing2(
    type: Class<out ErrorResponseModel>,
    response: Response<ResponseBody>
): RetrofitThrowable {
    //failed response
    val error = converter(type, response.errorBody())

    if (error != null) {
        error.code = response.code()
        return RetrofitThrowable(error, error.errorMessage)
    } else {
        try {
            val errorObj = type.getConstructor().newInstance()
            errorObj.code = response.code()
            errorObj.errorCode = Constants.ERROR_RESPONSE_EMPTY_ERROR_CODE
            errorObj.errorMessage = Constants.GENERAL_ERROR_MESSAGE
            return RetrofitThrowable(errorObj, errorObj.errorMessage)
        } catch (e: Exception) {
            //e.printStackTrace()
        }

        val errorResponse = ErrorResponseModel().apply {
            code = response.code()
            errorCode = Constants.ERROR_RESPONSE_EMPTY_ERROR_CODE
            errorMessage = Constants.GENERAL_ERROR_MESSAGE
        }
        return RetrofitThrowable(errorResponse, errorResponse.errorMessage)
    }
}

fun <T> converter(type: Class<T>, body: ResponseBody?): T? {

    if (body == null)
        return null

    val converter = RetrofitObject
        .retrofitBodyConverter<T>(type, arrayOfNulls<Annotation>(0))
    try {
        return converter.convert(body)
    } catch (ex: IOException) {
        throw ex
        return null
    }

}

fun <E : ErrorResponseModel> errorsHandling(errorModel: Class<E>, throwable: Throwable): E {
    val error: ErrorResponseModel
    appApiLog(text = "errorsHandling, + ${throwable}")
    appApiLog(text = "errorsHandling,class simpleName + ${throwable.javaClass.simpleName}")
    appApiLog(text = "errorsHandling, message + ${throwable.message}")



    if (throwable is ConnectException) {
        error = ErrorResponseModel().apply {
            errorCode = Constants.CONNECT_ERROR_CODE
            errorMessage = Constants.CONNECT_ERROR_MESSAGE
        }
    } else if (throwable is SocketTimeoutException) {
        error = ErrorResponseModel().apply {
            errorCode = Constants.CONNECT_TIME_OUT_ERROR_CODE
            errorMessage = Constants.CONNECT_TIME_OUT_ERROR_MESSAGE
        }
    } else if (throwable is UnknownHostException) {
        error = ErrorResponseModel().apply {
            errorCode = Constants.CONNECT_ADDRESS_ERROR_CODE
            errorMessage = Constants.CONNECT_ADDRESS_ERROR_MESSAGE
        }
    } else if (throwable is RetrofitThrowable)
        error = throwable.error
    else {
        val text = "HeadsUp ................ new Error Not Handling................" +
                "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                "error type: ${throwable.javaClass.simpleName}\n" +
                "error message: ${throwable.message}\n" +
                "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
        appApiLog(text = text)
        error = ErrorResponseModel().apply {
            errorCode = Constants.ERROR_RESPONSE_EMPTY_ERROR_CODE
            errorMessage = Constants.GENERAL_ERROR_MESSAGE
        }
    }
    if (errorModel == error.javaClass) {
        return error as E
    } else {
        try {
            val errorObj = errorModel.getConstructor().newInstance()
            errorObj.errorCode = error.errorCode
            errorObj.errorMessage = error.errorMessage
            return errorObj
        } catch (e: Exception) {
            //e.printStackTrace()
            val errorMess = "-------------------Heads up ------------------" +
                    "unExpected ErrorModel type. " +
                    "cause: try generate errorModel of type ${errorModel.javaClass} " +
                    "but ${e.javaClass} fired due to ${e.message} " +
                    "---- this may happen when " +
                    "${errorModel.javaClass} hove no empty constrictor, " +
                    "or class is final."
            throw IllegalArgumentException(errorMess, throwable)
        }
    }
}

fun appApiLog(text: String, tag: String = "appapi") {
//    Log.d(tag,text)
    println(text)
}
