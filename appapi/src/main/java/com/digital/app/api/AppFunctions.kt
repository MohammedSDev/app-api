package com.digital.app.api

import android.util.Base64
import android.webkit.MimeTypeMap
import com.digital.app.*
import com.digital.app.config.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer
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

class AppRequest(private val disposable: Disposable,
                 private val networkStatus: ((status: AppNetworkStatus) -> Unit)?
):Disposable{
    override fun isDisposed() = disposable.isDisposed

    override fun dispose() {
        disposable.dispose()
    }

    fun cancel(onCancelStatus:Boolean = true) {
        disposable.dispose()
        if(onCancelStatus)
            networkStatus?.invoke(AppNetworkStatus.OnCancel(null,null))
    }

    fun onCancelStatus(message:String? = null,tag:Any? = null) {
        networkStatus?.invoke(AppNetworkStatus.OnCancel(message,tag))
    }
}
class AppCompositeDisposable :  Disposable, DisposableContainer{
    private val comD = CompositeDisposable()
    private val comDisKey = hashMapOf<String,AppRequest>()

    override fun add(d: Disposable): Boolean {
        return comD.add(d)
    }

    /**
     * Adds a AppRequest to this container or cancel it if the
     * container has been disposed.
     * @param request the AppRequest to add, not null
     * @param override if true,cancel the exist AppRequest with same key.
     * @return true if successful, false if this container has been disposed
     * @throws NullPointerException if {@code request} is null
     */
    fun add(key:String,request: AppRequest,override:Boolean = true): Boolean {
        if (override){
            comDisKey[key]?.dispose()
        }
        comDisKey[key] = request
        return comD.add(request)
    }
    fun get(key:String):AppRequest?{
        return comDisKey[key]
    }

    /**
     * Removes and disposes the given disposable if it is part of this
     * container.
     * @param disposable the disposable to remove and dispose, not null
     * @return true if the operation was successful
     */
    override fun remove(d: Disposable): Boolean {
        return comD.remove(d)
    }

    /**
     * Removes and disposes the given disposable if it is part of this
     * container.
     * @param key the disposable key to remove and dispose, not null
     * @return true if the operation was successful
     */
    @Synchronized fun remove(key: String): Boolean {
        if(isDisposed)
            return false
        val dis = comDisKey[key] ?: return false
        comDisKey.remove(key)
        return comD.remove(dis)
    }

    /**
     * cancel the given AppRequest if it is part of this
     * container.
     * @param key the appRequest key to cancel, not null
     * @return true if the operation was successful
     */
    @Synchronized fun cancel(key: String): Boolean {
      return remove(key)
    }

    override fun delete(d: Disposable): Boolean {
        if(d is AppRequest) {
            if(comDisKey.containsValue(d)){
                comDisKey.keys.iterator().forEach {
                    if(comDisKey.get(it)?.equals(d) == true){
                        comDisKey.remove(it)
                        return@forEach
                    }
                }
            }
        }
        return comD.delete(d)
    }

    override fun isDisposed(): Boolean {
        return comD.isDisposed
    }

    fun cancelAll(){
        dispose()
    }
    override fun dispose() {
        comDisKey.clear()
        comD.dispose()
    }

}
open class AppFunctions(val method: AppMethod, val appRequestParam: AppRequestParam) {


    var onSuccess: ((response: ResponseModel) -> Unit)? = null
        protected set
    var onSuccessStatus: ((response: ResponseModel,networkStatus:((state:AppNetworkStatus) ->Unit)?) -> Unit)? = null
        protected set

    var onError: ((response: ErrorResponseModel) -> Unit)? = null
        protected set

    var onErrorStatus: ((response: ErrorResponseModel,networkStatus:((state:AppNetworkStatus) ->Unit)?) -> Unit)? = null
        protected set

    var networkStatus: ((status: AppNetworkStatus) -> Unit)? = null
        protected set

    fun preRequest(block: AppRequestParam.() -> Unit): AppFunctions {
        block(appRequestParam)
        return this
    }

    open fun onSuccess(block: (response: ResponseModel) -> Unit): AppFunctions {
        onSuccess = block
        return this
    }

    open fun onSuccess(block: (response: ResponseModel,status:((state:AppNetworkStatus)->Unit)? ) -> Unit): AppFunctions {
        onSuccessStatus = block
        return this
    }

    open fun onError(block: (response: ErrorResponseModel) -> Unit): AppFunctions {
        onError = block
        return this
    }


    open fun onError(block: (response: ErrorResponseModel,status:((state:AppNetworkStatus)->Unit)?) -> Unit): AppFunctions {
        onErrorStatus = block
        return this
    }

    fun onStatusChange(block: (status: AppNetworkStatus) -> Unit): AppFunctions {
        networkStatus = block
        return this
    }




    inline fun <reified R : ResponseModel> call(): AppRequest {


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
                    handleDataPacing2<R>(errorModel ?: Constants.errorModel,it)
                }
            if (observeOnMainThread ?: Constants.OBSERVER_ON_MAIN_THREAD) {
                ob2 = ob2.observeOn(AndroidSchedulers.mainThread())
            }
            val dis = ob2.doOnSubscribe {
                if (handleNetworkStatus)
                    networkStatus?.invoke(AppNetworkStatus.InProgress())
            }
                .subscribe({
                    onSuccessStatus?.invoke(it,networkStatus) ?: onSuccess?.invoke(it)
                    if (handleNetworkStatus)
                        networkStatus?.invoke(AppNetworkStatus.OnSuccess())
                }, {
                    onErrorStatus?.invoke(errorsHandling(it),networkStatus) ?: onError?.invoke(errorsHandling(it))
                    if (handleNetworkStatus)
                        networkStatus?.invoke(AppNetworkStatus.OnError())
                }, {})
            return AppRequest(dis,networkStatus)
        }

    }

    fun download(file: File): AppRequest {

        with(appRequestParam) {


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
            return AppRequest(dis,networkStatus)
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

infix fun String.toRequest(value: String): RequestBody = createRequestPart(value)
// url = file path or whatever suitable URL you want.
fun getMimeType(url: String): String? {
    if (url.isEmpty()) return null
    val file = File(url)
    val ins = BufferedInputStream(FileInputStream(file))
    val mimeType = URLConnection.guessContentTypeFromStream(ins)

    if (mimeType.isNotEmpty())
        return mimeType

    var type: String? = null

    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())
    } else {
        type = "*/*"
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
