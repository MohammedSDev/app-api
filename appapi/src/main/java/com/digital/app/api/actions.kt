package com.digital.app.api

import com.digital.app.*
import java.io.File

fun <T, E> post(
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


fun <T, E> put(
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

fun <T, E> get(
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

fun <T, E> delete(
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


fun <T, E, A : AppFunctions<T, E>> post(
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

fun <T, E, A : AppFunctions<T, E>> get(
	customAppFunction: Class<A>,
	responseModel: Class<T>,
	errorMode: Class<E>
): A {
	val appReq = AppRequestParam("")
	val instance2 =
		customAppFunction.getConstructor(AppMethod::class.java, AppRequestParam::class.java)
			.newInstance(AppMethod.GET, appReq)
	instance2.responseModel = responseModel
	instance2.errorModel = errorMode

	return instance2
}

fun <T, E, A : AppFunctions<T, E>> put(
	customAppFunction: Class<A>,
	responseModel: Class<T>,
	errorMode: Class<E>
): A {
	val appReq = AppRequestParam("")
	val instance2 =
		customAppFunction.getConstructor(AppMethod::class.java, AppRequestParam::class.java)
			.newInstance(AppMethod.PUT, appReq)
	instance2.responseModel = responseModel
	instance2.errorModel = errorMode

	return instance2
}

fun <T, E, A : AppFunctions<T, E>> delete(
	customAppFunction: Class<A>,
	responseModel: Class<T>,
	errorMode: Class<E>
): A {
	val appReq = AppRequestParam("")
	val instance2 =
		customAppFunction.getConstructor(AppMethod::class.java, AppRequestParam::class.java)
			.newInstance(AppMethod.DELETE, appReq)
	instance2.responseModel = responseModel
	instance2.errorModel = errorMode

	return instance2
}

/**
 * download file
 * @param file: destination file to download into it.
 * @param isAsync: true to download in different thread.
 * @param isLargeFile: true to try download file screamingly
 *
 * you can handle download process status in InProgress status.
 * library passes nullable DownloadProcess? in tag key of InProgress Status.
 *
 * */
fun download(
	file: File,
	isAsync: Boolean,
	appRequestParam: AppRequestParam,
	isLargeFile: Boolean,
	onSuccess: (r: DownloadModel) -> Unit,
	onStatus: (s: AppNetworkStatus) -> Unit,
	onError: (e: ErrorResponseModel) -> Unit
) {

	AppFunctions<DownloadModel, ErrorResponseModel>(AppMethod.GET, appRequestParam).also {
		it.responseModel = DownloadModel::class.java
		it.errorModel = ErrorResponseModel::class.java
	}.also {
		it.isDownloadAsync = isAsync
		it.isLargeFile = isLargeFile
	}
		.onSuccess(onSuccess)
		.onError(onError)
		.onStatusChange(onStatus)
		.download(file)
}


//refrences:
//https://cumulocity.com/guides/reference/real-time-notifications/
//https://developer.salesforce.com/docs/atlas.en-us.api_streaming.meta/api_streaming/using_streaming_api_client_connection.htm