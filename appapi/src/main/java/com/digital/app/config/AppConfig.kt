package com.digital.app.config

import android.app.Application
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

class AppConfig {
	val DEBUG_LEVEL_NONE = 0
	val DEBUG_LEVEL_BASIC = 1
	val DEBUG_LEVEL_HEADERS = 2
	val DEBUG_LEVEL_BODY = 3

	var OK_HTTP_CLIENT: OkHttpClient.Builder? = null
	var OK_HTTP_CLIENT_KEEP_PURE: Boolean = Constants.OK_HTTP_CLIENT_KEEP_PURE
	var CUSTOM_GSON_CONVERTER: GsonBuilder? = null
	var CUSTOM_GSON_CONVERTER_KEEP_PURE: Boolean = Constants.CUSTOM_GSON_CONVERTER_KEEP_PURE
	var BASE_URL = ""
	var TIMEOUT_UNIT = TimeUnit.SECONDS
	var CONNECT_TIMEOUT: Long = 20//TimeUnit.SECONDS
	var READ_TIMEOUT: Long = 20//TimeUnit.SECONDS
	var WRITE_TIMEOUT: Long = 30//TimeUnit.SECONDS
	var DEBUG_LEVEL: Int = DEBUG_LEVEL_BODY
	var OBSERVER_ON_MAIN_THREAD: Boolean = true
	var ADAPTERS: List<AppApiAdapterComponent> = listOf()
//    var errorModel : Class<out ErrorResponseModel> = Constants.errorModel
//    var customAppFunction: Class<out AppFunctions<ResponseModel,ErrorResponseModel>>? = null


	var GENERAL_ERROR_MESSAGE = Constants.GENERAL_ERROR_MESSAGE
	var CONNECT_ERROR_MESSAGE = Constants.CONNECT_ERROR_MESSAGE
	var CONNECT_TIME_OUT_ERROR_MESSAGE = Constants.CONNECT_TIME_OUT_ERROR_MESSAGE
	var CONNECT_ADDRESS_ERROR_MESSAGE = Constants.CONNECT_ADDRESS_ERROR_MESSAGE

	var certificatePinner: CertificatePinner? = null

	var cashStrategy: Int = Constants.NONE_STRATEGY
		private set
	internal var weakContext: WeakReference<Context>? = null
		private set

	/**
	 * set cache strategy
	 *  Constants.NONE_STRATEGY, (Default)
	 *  Constants.FORCE_STRATEGY,
	 *  Constants.ON_FAILED_STRATEGY
	 * */
	fun setCashStrategy(strategy: Int, context: Context?) {
		cashStrategy = strategy
		if (context != null)
			weakContext = WeakReference(context)
		else
			weakContext = null
	}
}

fun appConfig(config: AppConfig.() -> Unit) {
	val appConfig = AppConfig()
	config.invoke(appConfig)
	//updateConstants
	Constants.apply {
		OK_HTTP_CLIENT = appConfig.OK_HTTP_CLIENT
		OK_HTTP_CLIENT_KEEP_PURE = appConfig.OK_HTTP_CLIENT_KEEP_PURE
		CUSTOM_GSON_CONVERTER = appConfig.CUSTOM_GSON_CONVERTER
		CUSTOM_GSON_CONVERTER_KEEP_PURE = appConfig.CUSTOM_GSON_CONVERTER_KEEP_PURE
		BASE_URL = appConfig.BASE_URL
		TIMEOUT_UNIT = appConfig.TIMEOUT_UNIT
		CONNECT_TIMEOUT = appConfig.CONNECT_TIMEOUT
		READ_TIMEOUT = appConfig.READ_TIMEOUT
		WRITE_TIMEOUT = appConfig.WRITE_TIMEOUT
		OBSERVER_ON_MAIN_THREAD = appConfig.OBSERVER_ON_MAIN_THREAD
		ADAPTERS = appConfig.ADAPTERS
//        errorModel  = appConfig.errorModel
//        customAppFunction  = appConfig.customAppFunction

		DEBUG_LEVEL = when (appConfig.DEBUG_LEVEL) {
			appConfig.DEBUG_LEVEL_NONE -> HttpLoggingInterceptor.Level.NONE
			appConfig.DEBUG_LEVEL_BASIC -> HttpLoggingInterceptor.Level.BASIC
			appConfig.DEBUG_LEVEL_HEADERS -> HttpLoggingInterceptor.Level.HEADERS
			appConfig.DEBUG_LEVEL_BODY -> HttpLoggingInterceptor.Level.BODY
			else -> HttpLoggingInterceptor.Level.BODY
		}


		CONNECT_ERROR_MESSAGE = appConfig.GENERAL_ERROR_MESSAGE
		CONNECT_ERROR_MESSAGE = appConfig.CONNECT_ERROR_MESSAGE
		CONNECT_TIME_OUT_ERROR_MESSAGE = appConfig.CONNECT_TIME_OUT_ERROR_MESSAGE
		CONNECT_ADDRESS_ERROR_MESSAGE = appConfig.CONNECT_ADDRESS_ERROR_MESSAGE


		CERTIFICATE_PINNER = appConfig.certificatePinner


		CASH_STRATEGY = appConfig.cashStrategy
		CONTEXT = appConfig.weakContext


	}
}