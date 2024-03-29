package com.digital.app


import android.util.Log
import com.digital.app.config.Constants
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import retrofit2.Response
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.UnknownHostException


fun <T>
  handleDataPacing2(
  responseType: Class<T>,
  type: Class<*>,
  response: Response<ResponseBody>
): T {

  if (response.isSuccessful) {
    //success response
//        val result = converter<T>(T::class.java,response.body())
    val result = converter<T>(responseType, response.body())
    if (result != null) {
      if (result is ResponseModel)
        result.code = response.code()
      return result
    } else {
      val text = "HeadsUp ..<>.............. new Error, body empty ................" +
        "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
        "result: ${result}\n" +
        "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
      appApiLog(text)
      val error = create(
        Constants.RESPONSE_EMPTY_ERROR_CODE,
        Constants.GENERAL_ERROR_MESSAGE,
        IllegalStateException("response parsed is null,code: ${response.code()}")
      )
      throw RetrofitThrowable(error, error.message)
    }
  } else {
    throw failedResponseParcing2(type, response)

  }
}

fun failedResponseParcing2(
  type: Class<*>,
  response: Response<ResponseBody>
): RetrofitThrowable {
  //failed response
  val error = converter(type, response.errorBody())

  if (error != null) {
    return if (error is ErrorResponseModel) {
      error.code = response.code()
      RetrofitThrowable(error, error.message)
    } else {
      RetrofitThrowable(error, response.message())
    }
  } else {
    try {
      val errorObj = type.getConstructor().newInstance()
      return if (errorObj is ErrorResponseModel) {
        errorObj.code = response.code()
        errorObj.message = response.message()?.toString() ?: Constants.GENERAL_ERROR_MESSAGE
        errorObj.throwable =
          IllegalStateException("failure response parsed is null,code: ${response.code()}")
        RetrofitThrowable(errorObj, errorObj.message)
      } else {
        RetrofitThrowable(
          errorObj,
          response.message()?.toString() ?: Constants.GENERAL_ERROR_MESSAGE
        )
      }

    } catch (e: Exception) {
      appApiLog(e)
      val errorResponse = create(response.code(), Constants.GENERAL_ERROR_MESSAGE, e)
      return RetrofitThrowable(errorResponse, errorResponse.message)
    }
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

fun <E> errorsHandling(errorModel: Class<E>, throwable: Throwable): E {
  val error: ErrorResponseModel
  appApiLog(text = "errorsHandling, + ${throwable}")
  appApiLog(text = "errorsHandling,class simpleName + ${throwable.javaClass.simpleName}")
  appApiLog(text = "errorsHandling, message + ${throwable.message}")



  if (throwable is ConnectException) {
    error = create(Constants.CONNECT_ERROR_CODE, Constants.CONNECT_ERROR_MESSAGE, throwable)

  } else if (throwable is SocketTimeoutException) {
    error = create(
      Constants.CONNECT_TIME_OUT_ERROR_CODE,
      Constants.CONNECT_TIME_OUT_ERROR_MESSAGE,
      throwable
    )

  } else if (throwable is UnknownHostException) {
    error = create(
      Constants.CONNECT_ADDRESS_ERROR_CODE,
      Constants.CONNECT_ADDRESS_ERROR_MESSAGE,
      throwable
    )
  } else if (throwable is RetrofitThrowable && throwable.error is ErrorResponseModel)
    error = throwable.error
  else {
    val text = "HeadsUp ................ new Error Not Handling................" +
      "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
      "error type: ${throwable.javaClass.simpleName}\n" +
      "error message: ${throwable.message}\n" +
      "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
    appApiLog(text = text)
    error =
      create(Constants.ERROR_RESPONSE_EMPTY_ERROR_CODE, Constants.GENERAL_ERROR_MESSAGE, throwable)

  }
  if (errorModel == error.javaClass) {
    return error as E
  } else {
    try {
      val errorObj = errorModel.getConstructor().newInstance()
      if (errorObj is ErrorResponseModel) {
        errorObj.code = error.code
        errorObj.message = error.message
        errorObj.throwable = error.throwable
      }
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

fun appApiLog(text: String?, tag: String = "appapi") {
  text ?: return
  if (Constants.DEBUG_LEVEL != HttpLoggingInterceptor.Level.NONE)
    Log.d(tag, text)
//    println(text)
}

fun appApiLog(exception: Exception) {
  if (Constants.DEBUG_LEVEL != HttpLoggingInterceptor.Level.NONE)
    exception.printStackTrace()
}


private fun create(code: Int, message: String, throwable: Throwable? = null) =
  object : ErrorResponseModel {
    private var c = code
    private var mes = message
    private var err: Throwable? = throwable
    override var message: String
      get() = mes
      set(value) {
        mes = value
      }
    override var throwable: Throwable?
      get() = err
      set(value) {
        err = value
      }
    override var code: Int
      get() = c
      set(value) {
        c = value
      }
  }