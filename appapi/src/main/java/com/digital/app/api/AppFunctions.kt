package com.digital.app.api

import android.util.Base64
import android.webkit.MimeTypeMap
import com.digital.app.*
import com.digital.app.config.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.*
import java.net.URLConnection
import java.util.concurrent.TimeUnit

enum class AppMethod {
    POST, GET, PUT, DELETE
}

class AppRequest(
    internal val disposable: Disposable,
    private val networkStatus: ((status: AppNetworkStatus) -> Unit)?
) {

    fun isDisposed() = disposable.isDisposed

    fun dispose() {
        disposable.dispose()
    }

    fun cancel(onCancelStatus: Boolean = true) {
        disposable.dispose()
        if (onCancelStatus)
            networkStatus?.invoke(AppNetworkStatus.OnCancel(null, null))
    }

    fun onCancelStatus(message: String? = null, tag: Any? = null) {
        networkStatus?.invoke(AppNetworkStatus.OnCancel(message, tag))
    }
}

class AppCompositeDisposable {
    private val comD = CompositeDisposable()
    private val comDisKey = hashMapOf<String, AppRequest>()

    fun add(d: Disposable): Boolean {
        return comD.add(d)
    }

    fun add(key: String, d: Disposable, override: Boolean = true): Boolean {
        return add(key, AppRequest(d, {}), override)
    }

    /**
     * Adds a AppRequest to this container or cancel it if the
     * container has been disposed.
     * @param request the AppRequest to add, if null,nothing will harping,and thus return false.
     * @param override if true,cancel the exist AppRequest with same key.
     * @return true if successful, false if this container has been disposed
     * @throws NullPointerException if {@code request} is null
     */
    fun add(key: String, request: AppRequest?, override: Boolean = true): Boolean {
        request ?: return false
        if (override) {
            comDisKey[key]?.dispose()
        }
        comDisKey[key] = request
        return comD.add(request.disposable)
    }

    fun get(key: String): AppRequest? {
        return comDisKey[key]
    }

    /**
     * Removes and disposes the given disposable if it is part of this
     * container.
     * @param disposable the disposable to remove and dispose, not null
     * @return true if the operation was successful
     */
    fun remove(d: Disposable): Boolean {
        return comD.remove(d)
    }

    /**
     * Removes and disposes the given disposable if it is part of this
     * container.
     * @param key the disposable key to remove and dispose, not null
     * @return true if the operation was successful
     */
    @Synchronized
    fun remove(key: String): Boolean {
        if (isDisposed())
            return false
        val dis = comDisKey[key] ?: return false
        comDisKey.remove(key)
        return comD.remove(dis.disposable)
    }

    /**
     * cancel the given AppRequest if it is part of this
     * container.
     * @param key the appRequest key to cancel, not null
     * @return true if the operation was successful
     */
    @Synchronized
    fun cancel(key: String): Boolean {
        return remove(key)
    }

    fun delete(d: Disposable): Boolean {
        val appRequet = comDisKey.filter { it.value.disposable == d }
        if (appRequet.isNotEmpty() && appRequet.keys.first().isNotEmpty()) {
            comDisKey.remove(appRequet.keys.first())
            /*comDisKey.keys.iterator().forEach {
                if (comDisKey.get(it)?.equals(d) == true) {
                    comDisKey.remove(it)
                    return@forEach
                }
            }*/
        }

        return comD.delete(d)
    }

    fun isDisposed(): Boolean {
        return comD.isDisposed
    }

    fun cancelAll() {
        dispose()
    }

    fun dispose() {
        comDisKey.clear()
        comD.dispose()
    }

    fun contain(key: String): Boolean = comDisKey.containsKey(key)

    val size get() = comD.size()

}

open class AppFunctions<T, E : ErrorResponseModel>(
    private val method: AppMethod,
    private val appRequestParam: AppRequestParam
) {

    /**
     * download file in current thread
     * */
    internal var isDownloadAsync: Boolean = false
    internal var isLargeFile: Boolean = false

    internal lateinit var errorModel: Class<E>
    internal lateinit var responseModel: Class<T>
    protected var onSuccess: ((response: T) -> Unit)? = null

    protected var onSuccessStatus: ((response: T, networkStatus: ((state: AppNetworkStatus) -> Unit)?) -> Unit)? =
        null

    protected var onError: ((response: E) -> Unit)? = null

    protected var onErrorStatus: ((response: E, networkStatus: ((state: AppNetworkStatus) -> Unit)?) -> Unit)? =
        null

    protected var networkStatus: ((status: AppNetworkStatus) -> Unit)? = null

    fun preRequest(block: AppRequestParam.() -> Unit): AppFunctions<T, E> {
        block(appRequestParam)
        return this
    }

    open fun onSuccess(block: (response: T) -> Unit): AppFunctions<T, E> {
        onSuccess = block
        return this
    }

    open fun onSuccess(block: (response: T, status: ((state: AppNetworkStatus) -> Unit)?) -> Unit): AppFunctions<T, E> {
        onSuccessStatus = block
        return this
    }

    open fun onError(block: (response: E) -> Unit): AppFunctions<T, E> {
        onError = block
        return this
    }


    open fun onError(block: (response: E, status: ((state: AppNetworkStatus) -> Unit)?) -> Unit): AppFunctions<T, E> {
        onErrorStatus = block
        return this
    }

    fun onStatusChange(block: (status: AppNetworkStatus) -> Unit): AppFunctions<T, E> {
        networkStatus = block
        return this
    }


    fun call(): AppRequest {


        with(appRequestParam) {

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
                            multiBodyParam.map { Pair(it.key, it.value.requestBody) }.toMap(),
                            multiPFilesList,
                            queryParam,
                            headerParam
                        )
                    }
                    AppMethod.PUT -> {
                        apiService.putMultiPart(
                            url,
                            multiBodyParam.map { Pair(it.key, it.value.requestBody) }.toMap(),
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
                        apiService.delete(url, queryParam, headerParam)
                    }
                }
            }
            var ob2 = ob
                .delay(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .map {
                    handleDataPacing2<T>(responseModel, errorModel, it)
                }
            if (observeOnMainThread ?: Constants.OBSERVER_ON_MAIN_THREAD) {
                ob2 = ob2.observeOn(AndroidSchedulers.mainThread())
            }
            val dis = ob2.doOnSubscribe {
                if (handleNetworkStatus)
                    networkStatus?.invoke(AppNetworkStatus.InProgress())
            }
                .subscribe({
                    if (handleNetworkStatus) {
                        onSuccessStatus?.invoke(it, networkStatus) ?: onSuccess?.invoke(it as T)
                        onSuccessStatus ?: networkStatus?.invoke(AppNetworkStatus.OnSuccess())
                    } else {
                        onSuccessStatus?.invoke(it, {}) ?: onSuccess?.invoke(it as T)
                    }
                }, {
                    if (handleNetworkStatus) {
                        onErrorStatus?.invoke(errorsHandling<E>(errorModel, it), networkStatus)
                            ?: onError?.invoke(errorsHandling(errorModel, it))
                        onErrorStatus ?: networkStatus?.invoke(AppNetworkStatus.OnError())
                    } else {
                        onErrorStatus?.invoke(errorsHandling<E>(errorModel, it), {})
                            ?: onError?.invoke(errorsHandling(errorModel, it))
                    }
                }, {})
            return AppRequest(dis, networkStatus)
        }

    }

    fun download(file: File): AppRequest {

        with(appRequestParam) {


            var ob2: Observable<ResponseBody> = (if (isLargeFile)
                RetrofitObject.retrofit.downloadStreamingFileUrlSync(url, headerParam)
            else
                RetrofitObject.retrofit.downloadFileUrlSync(url, headerParam)
                    )
                .delay(delay, TimeUnit.MILLISECONDS)

            if (isDownloadAsync)
                ob2 = ob2.subscribeOn(Schedulers.computation())
            if (observeOnMainThread ?: Constants.OBSERVER_ON_MAIN_THREAD) {
                ob2 = ob2.observeOn(AndroidSchedulers.mainThread())
            }
            if (handleNetworkStatus)
                ob2 = ob2.doOnSubscribe {
                    networkStatus?.invoke(AppNetworkStatus.InProgress())
                }
            val dis = ob2.subscribe({
                //                println("file connected, start write to disk. ... .")
                writeResponseBodyToDisk(it, file)
                if (handleNetworkStatus)
                    networkStatus?.invoke(AppNetworkStatus.OnSuccess())
                val res = if (responseModel == DownloadModel::class.java)
                    DownloadModel(file.absolutePath) as T
                else responseModel.getConstructor().newInstance()
                onSuccess?.invoke(res)
            }, { err ->
                if (handleNetworkStatus)
                    networkStatus?.invoke(AppNetworkStatus.OnError())
                val res = errorModel.getConstructor().newInstance()

                res.message = err.message ?: err.localizedMessage

                onError?.invoke(res)
            }, {})
            return AppRequest(dis, networkStatus)
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

                    if (appRequestParam.handleNetworkStatus)
                        networkStatus?.invoke(
                            AppNetworkStatus.InProgress(
                                null,
                                DownloadProcess(fileSizeDownloaded / fileSize * 100, fileSize)
                            )
                        )
//                    println("App-api,download: file download: $fileSizeDownloaded of $fileSize")
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


    private fun prepareFileToMultiPart(
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


    /**
     * don't use this. use `getMimeType` fun.
     * */
    @Deprecated("use `getMimeType` fun.")
    private fun getExtensionMimeType(path: String): String? {
        val extention = path.substring(path.lastIndexOf("."));
        val mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extention)
        val mimeType = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(mimeTypeMap)
        return mimeType
    }


}


fun createRequestPart(value: String): AppRequestBody {
    return AppRequestBody.create(
        MultipartBody.FORM, value
    )
}

infix fun String.toRequest(value: String): Pair<String, AppRequestBody> =
    Pair(this, createRequestPart(value))

// url = file path or whatever suitable URL you want.
fun getMimeType(url: String): String? {
    if (url.isEmpty()) return null
    val file = File(url)
    val ins = BufferedInputStream(FileInputStream(file))
    val mimeType = URLConnection.guessContentTypeFromStream(ins)

    if (mimeType?.isNotEmpty() == true)
        return mimeType

    var type: String? = null

    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())
    } else {
        //type = "*/*"
    }
    return type

    //val uri = Uri.parse(url)
    /*if (context != null
        && uri.scheme?.equals(ContentResolver.SCHEME_CONTENT) == true
    ) {
        val cr = context.applicationContext.contentResolver
        type = cr.getType(uri)
    }
    else*/
}

fun File.toBase64(): String? {
    val fileInputStreamReader = FileInputStream(this)
    val bytes = ByteArray(this.length().toInt())
    fileInputStreamReader.read(bytes)
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}
