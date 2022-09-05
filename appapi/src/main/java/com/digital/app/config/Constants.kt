package com.digital.app.config

import android.content.Context
import com.digital.app.ErrorResponseModel
import com.digital.app.ResponseModel
import com.digital.app.api.AppFunctions
import com.google.gson.GsonBuilder
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass


object Constants {
	var OK_HTTP_CLIENT: OkHttpClient.Builder? = null
		internal set
	var OK_HTTP_CLIENT_KEEP_PURE: Boolean = false
		internal set
	var CUSTOM_GSON_CONVERTER: GsonBuilder? = null
		internal set
	var CUSTOM_GSON_CONVERTER_KEEP_PURE: Boolean = false
		internal set
	var BASE_URL = ""
		internal set
	var TIMEOUT_UNIT = TimeUnit.SECONDS
		internal set
	var CONNECT_TIMEOUT: Long = 20
		//TimeUnit.SECONDS
		internal set
	var READ_TIMEOUT: Long = 20
		//TimeUnit.SECONDS
		internal set
	var WRITE_TIMEOUT: Long = 30
		//TimeUnit.SECONDS
		internal set
	var DEBUG_LEVEL: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
		internal set
	var OBSERVER_ON_MAIN_THREAD: Boolean = true
		internal set
	var ADAPTERS: List<AppApiAdapterComponent> = listOf()
		internal set
//    var errorModel : Class<out ErrorResponseModel> = ErrorResponseModel::class.java
//        internal  set
//    var customAppFunction : Class<out AppFunctions<ResponseModel,ErrorResponseModel>>? = null
//        internal  set

	const val FORCE_STRATEGY = 10
	const val NONE_STRATEGY = 20
	const val ON_FAILED_STRATEGY = 30

	var CASH_STRATEGY: Int = NONE_STRATEGY
		internal set
	internal var CONTEXT: WeakReference<Context>? = null



	const val CONNECT_ERROR_CODE = 3021
	const val CONNECT_TIME_OUT_ERROR_CODE = 3022
	const val CONNECT_ADDRESS_ERROR_CODE = 3023
	const val RESPONSE_EMPTY_ERROR_CODE = 3032
	const val ERROR_RESPONSE_EMPTY_ERROR_CODE = 3033


	internal var CERTIFICATE_PINNER: CertificatePinner? = null

	var GENERAL_ERROR_MESSAGE = "Some thing went wrong. try later"
		internal set
	var CONNECT_ERROR_MESSAGE = "Check your connection."
		internal set
	var CONNECT_TIME_OUT_ERROR_MESSAGE = "Time out, try later."
		internal set
	var CONNECT_ADDRESS_ERROR_MESSAGE = "Host address could not be determined."
		internal set

}