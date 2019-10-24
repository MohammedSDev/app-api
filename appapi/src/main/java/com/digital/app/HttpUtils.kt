package com.digital.app


import com.digital.app.config.Constants
import okhttp3.ResponseBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import retrofit2.Response
import java.net.UnknownHostException


inline fun <reified T : ResponseModel,reified E:ErrorResponseModel> handleDataPacing2(response: retrofit2.Response<ResponseBody>): T {
    println("---------------------------------------------------parcing2OnAPpFunction")
    println("response.isSuccessful:${response.isSuccessful}")

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
                errorMessage = "Some thing went wrong. try later"
            }
            throw RetrofitThrowable(error, error.errorMessage.toString())
        }
    } else {
//        val errorTT:Any?
//        val body11 = response.errorBody()
//        if (body11 == null)
//        {
//            println("body null")
//        }
//
//        val converter22 = RetrofitObject
//            .retrofitBodyConverter<T>(T::class.java, arrayOfNulls<Annotation>(0))
//        try {
//            errorTT = converter22.convert(body11)
//        } catch (ex: IOException) {
//            throw ex
//
//        }
//        val error11 = converter<Er>(response.errorBody())
//        val error22 = converter<E>(response.errorBody())
//        val error33 = converter<ErrorResponseModel>(response.errorBody())
        println(".")
        println(".")
        println(".")
        println(".")
        println(".")
        println(".")
        println("---------------------------------------------------parcing2OnAPpFunction")
        println(E::class.java.newInstance())
        println(E::class.java.newInstance())
        println(E::class.java.newInstance().errorMessage)
//        throw RetrofitThrowable(ErrorResponseModel(),"empty")
        throw failedResponseParcing2<E>(response)

    }
}
inline fun <reified E : ErrorResponseModel>failedResponseParcing2(response: Response<ResponseBody>): RetrofitThrowable {
    //failed response
    val error = converter<E>(response.errorBody())
//        val error =  converter<T>(response.errorBody())
    if (error != null)
        return RetrofitThrowable(error, error.errorMessage.toString())
    else {
        val errorResponse = ErrorResponseModel().apply {
            //            success = false
            errorMessage = "Some Thing Went wrong"
//                statusCode = error?.statusCode
        }
        return RetrofitThrowable(errorResponse, errorResponse.errorMessage.toString())
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


    // if
    if (throwable is ConnectException) {
        error = ErrorResponseModel().apply {
            errorCode = Constants.CONNECT_ERROR_CODE
//            message = "Opps.Kindly check your Connection"
            /*messageRes = R.string.kindly_check_your_connection*/
        }
    }
    else if (throwable is SocketTimeoutException) {
        error = ErrorResponseModel().apply {
            errorCode = Constants.CONNECT_ERROR_CODE
//            message = "Opps.Kindly check your Connection"
            /*messageRes = R.string.kindly_check_your_connection*/
        }
    }
    else if (throwable is UnknownHostException) {
        error = ErrorResponseModel().apply {
            errorCode = Constants.CONNECT_ERROR_CODE
            /*messageRes = R.string.kindly_check_your_connection*/
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
//            message = "Some thing went wrong. try later"
            /*messageRes = R.string.some_thing_went_wrong*/
        }
    }

//    error.success = false
    return error
}

fun appApiLog(text:String,tag:String="appapi"){
//    Log.d(tag,text)
    println(text)
}
