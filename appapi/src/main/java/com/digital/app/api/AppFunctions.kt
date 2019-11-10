package com.digital.app.api

import android.webkit.MimeTypeMap
import com.digital.app.*
import com.digital.app.config.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.*
import java.util.concurrent.TimeUnit

enum class AppMethod {
    POST, GET, PUT, DELETE
}

open class AppFunctions(val method: AppMethod, val appRequest: AppRequest) {


    var onSuccess: ((response: ResponseModel) -> Unit)? = null
        protected set
    var onError: ((response: ErrorResponseModel) -> Unit)? = null
        protected set
    var disposable: Disposable? = null

    var networkStatus: ((status: AppNetworkStatus) -> Unit)? = null
        protected set

    fun preRequest(block: AppRequest.() -> Unit): AppFunctions {
        block(appRequest)
        return this
    }

    open fun onSuccess(block: (response: ResponseModel) -> Unit): AppFunctions {
        onSuccess = block
        return this
    }

    open fun onError(block: (response: ErrorResponseModel) -> Unit): AppFunctions {
        onError = block
        return this
    }

    open fun onStatusChange(block: (status: AppNetworkStatus) -> Unit): AppFunctions {
        networkStatus = block
        return this
    }

    fun cancel() {
        disposable?.dispose()
    }


    inline fun <reified R : ResponseModel, reified E : ErrorResponseModel> call(): Disposable {


        with(appRequest) {

            val apiService = RetrofitObject.retrofit
            val ob = if (isMultiPart) {
                if (bodyParam.isNotEmpty() && multiBodyParam.isEmpty()) {
                    throw Throwable("use `multiBodyParam` instead of `bodyParam` with multiPart request ")
                }
                val multiPFilesList: MutableList<MultipartBody.Part> = mutableListOf()
                multiPartFiles?.forEach { fileModel ->
                    if (fileModel.file.exists()) {
                        multiPFilesList.add(
                            prepareFileToMultiPart(
                                fileModel.fileKeyName,
                                fileModel.file,
                                fileModel.mediaType
                            )
                        )
                    } else {
                        //log(text = "${fileModel.fileKeyName} file :${fileModel.file.absolutePath} not exist.skip it")
                    }
                }
                when (method) {
                    AppMethod.POST -> {
                        apiService.postMultiPart(
                            url,
                            multiBodyParam,
                            multiPFilesList,
                            queryParam,
                            headerParam
                        )
                    }
                    AppMethod.PUT -> {
                        apiService.putMultiPart(
                            url,
                            multiBodyParam,
                            multiPFilesList,
                            queryParam,
                            headerParam
                        )
                    }
                    AppMethod.GET -> {
                        throw Throwable("AppApi not support multipart request with GET method.")
                    }
                    AppMethod.DELETE -> {
                        throw Throwable("AppApi not support multipart request with DELETE method.")
                    }
                }
            } else {
                when (method) {
                    AppMethod.POST -> {
                        apiService.post(url, bodyParam, queryParam, headerParam)
                    }
                    AppMethod.PUT -> {
                        apiService.put(url, bodyParam, queryParam, headerParam)
                    }
                    AppMethod.GET -> {
                        apiService.get(url, queryParam, headerParam)
                    }
                    AppMethod.DELETE -> {
                        apiService.delete(url, bodyParam, queryParam, headerParam)
                    }
                }
            }
            var ob2 = ob
                .delay(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .map {
                    handleDataPacing2<R, E>(it)
                }
            if (observeOnMainThread ?: Constants.OBSERVER_ON_MAIN_THREAD) {
                ob2 = ob2.observeOn(AndroidSchedulers.mainThread())
            }
            val dis = ob2.doOnSubscribe {
                networkStatus?.invoke(AppNetworkStatus.InProgress)}
                .subscribe({
                    onSuccess?.invoke(it)
                    networkStatus?.invoke(AppNetworkStatus.OnSuccess)
                }, {
                    onError?.invoke(errorsHandling(it))
                    networkStatus?.invoke(AppNetworkStatus.OnError)
                }, {})
            disposable = dis
            return dis
        }

    }

    fun download(file: File): Disposable {

        with(appRequest) {


            var ob2 = RetrofitObject.retrofit.downloadFileUrlSync(url, headerParam)
                .delay(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())

            if (observeOnMainThread ?: Constants.OBSERVER_ON_MAIN_THREAD) {
                ob2 = ob2.observeOn(AndroidSchedulers.mainThread())
            }
            val dis = ob2.subscribe({
                //                println("file connected, start write to disk. ... .")
                writeResponseBodyToDisk(it, file)
                onSuccess?.invoke(ResponseModel())
            }, { err ->
                onError?.invoke(ErrorResponseModel().also {
                    it.errorMessage = err.message ?: err.localizedMessage
                })
            }, {})
            disposable = dis
            return dis
        }

    }


    private fun writeResponseBodyToDisk(body: ResponseBody, file: File): Boolean {
        try {
            // todo change the file location/name according to your needs
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)

                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0

                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)

                while (true) {
                    val read = inputStream!!.read(fileReader)

                    if (read == -1) {
                        break
                    }

                    outputStream!!.write(fileReader, 0, read)

                    fileSizeDownloaded += read.toLong()

//                    Log.d("mud", "file download: $fileSizeDownloaded of $fileSize")
                }

                outputStream!!.flush()

                return true
            } catch (e: IOException) {
//                return false
                throw e
            } finally {
                if (inputStream != null) {
                    inputStream!!.close()
                }

                if (outputStream != null) {
                    outputStream!!.close()
                }
            }
        } catch (e: IOException) {
            //return false
            throw e
        }

    }


    fun prepareFileToMultiPart(
        partName: String,
        file: File,
        mediaType: String
    ): MultipartBody.Part {

//    val mediaType = MediaType.parse(getContentResolver().getType(fileUri))
        // create RequestBody instance from file
        val requestFile = RequestBody.create(
            MediaType.parse(mediaType),
            file
        )

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }


    //don't use this. use `getMimeType` fun.
    private fun getExtensionMimeType(path: String): String? {
        val extention = path.substring(path.lastIndexOf("."));
        val mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extention)
        val mimeType = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(mimeTypeMap)
        return mimeType
    }


}

fun createRequestPart(value: String): RequestBody {
    return RequestBody.create(
        okhttp3.MultipartBody.FORM, value
    )
}

// url = file path or whatever suitable URL you want.
fun getMimeType(url: String): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}
