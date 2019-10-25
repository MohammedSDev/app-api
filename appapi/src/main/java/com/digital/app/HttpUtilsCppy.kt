package com.digital.app

/*

import android.util.Log
import com.digital.app.appConfig.Constants
import okhttp3.MediaType
import okhttp3.ResponseBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import okhttp3.RequestBody
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.File
import java.net.UnknownHostException
import kotlin.reflect.KFunction0


inline fun <reified T : ResponseModel> handleDataPacing(response: retrofit2.Response<ResponseBody>): T {

    if (response.isSuccessful) {
        //success response
        val result = converter<T>(response.body())
        if (result != null)
            return result
        else {
            val text = "HeadsUp ................ new Error, body empty ................" +
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
        throw failedResponseParcing(response)

    }
}
inline fun <reified T : ResponseModel,reified E:ErrorResponseModel> handleDataPacing2(response: retrofit2.Response<ResponseBody>): T {

    if (response.isSuccessful) {
        //success response
        val result = converter<T>(response.body())
        if (result != null)
            return result
        else {
            val text = "HeadsUp ................ new Error, body empty ................" +
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
        throw RetrofitThrowable(ErrorResponseModel(),"empty")
        //throw failedResponseParcing2<E>(response)

    }
}
class Er{

    val success:String? = null
    val error:String? = null
}
fun failedResponseParcing(response: Response<ResponseBody>): RetrofitThrowable {
    //failed response
    val error = converter<ErrorResponseModel>(response.errorBody())
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
inline fun <reified E : ErrorResponseModel>failedResponseParcing2(response: Response<ResponseBody>): RetrofitThrowable {
    //failed response

    val errorT = converter<Er>(response.errorBody())
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


*/
/**
 * handling
 * *//*


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
            */
/*messageRes = R.string.kindly_check_your_connection*//*

        }
    }
    else if (throwable is SocketTimeoutException) {
        error = ErrorResponseModel().apply {
            errorCode = Constants.CONNECT_ERROR_CODE
//            message = "Opps.Kindly check your Connection"
            */
/*messageRes = R.string.kindly_check_your_connection*//*

        }
    }
    else if (throwable is UnknownHostException) {
        error = ErrorResponseModel().apply {
            errorCode = Constants.CONNECT_ERROR_CODE
            */
/*messageRes = R.string.kindly_check_your_connection*//*

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
            */
/*messageRes = R.string.some_thing_went_wrong*//*

        }
    }

//    error.success = false
    return error
}


//multipart
fun createRequestPart(value: String): RequestBody {
    return RequestBody.create(
            okhttp3.MultipartBody.FORM, value)
}

fun prepareFileToMultiPart(partName: String, file: File,mediaType:String): MultipartBody.Part {
    // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
    // use the FileUtils to get the actual file by uri

//    val mediaType = MediaType.parse(getContentResolver().getType(fileUri))
    // create RequestBody instance from file
    val requestFile = RequestBody.create(
            MediaType.parse(mediaType),
            file
    )

    // MultipartBody.Part is used to send also the actual file name
    return MultipartBody.Part.createFormData(partName, file.getName(), requestFile)
}

*/
/*
*
*
    error type: JsonSyntaxException
    error message: java.lang.NumberFormatException: Expected an int but was 2.7 at line 1 column 719 path $.data.parcels[0].rating


    errorsHandling, + java.net.SocketException: Connection reset
errorsHandling,class simpleName + SocketException
errorsHandling, message + Connection reset


errorsHandling, + java.io.EOFException: \n not found: limit=1 content=0d…
errorsHandling,class simpleName + EOFException
errorsHandling, message + \n not found: limit=1 content=0d…


errorsHandling, + com.google.gson.stream.MalformedJsonException: Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 1 path $
2019-07-11 16:25:29.170 10488-10584/com.jisr.ess.dev D/jisr_log: errorsHandling,class simpleName + MalformedJsonException
2019-07-11 16:25:29.170 10488-10584/com.jisr.ess.dev D/jisr_log: errorsHandling, message + Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 1 path $


 errorsHandling, + java.net.SocketException: Connection reset
2019-07-25 11:32:26.889 14128-14181/com.jisr.ess.dev D/jisr_log: errorsHandling,class simpleName + SocketException
2019-07-25 11:32:26.889 14128-14181/com.jisr.ess.dev D/jisr_log: errorsHandling, message + Connection reset
 error type: SocketException
    error message: Connection reset

* *//*




private fun log(text:String,tag:String="appapi"){
    Log.d(tag,text)
}

fun appApiLog(text:String,tag:String="appapi"){
//    Log.d(tag,text)
    println(text)
}
*/
