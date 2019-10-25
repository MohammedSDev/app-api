package com.digital.app.api

import android.util.Log
import com.digital.app.*
import com.digital.app.config.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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

    fun cancel() {
        disposable?.dispose()
    }


    inline fun <reified R : ResponseModel, reified E : ErrorResponseModel> call(): Disposable {

        with(appRequest) {

            val apiService = RetrofitObject.retrofit
            val ob = when (method) {
                AppMethod.POST -> {
                    apiService.post(endPoint, bodyParam, queryParam, headerParam)
                }
                AppMethod.PUT -> {
                    apiService.put(endPoint, bodyParam, queryParam, headerParam)
                }
                AppMethod.GET -> {
                    apiService.get(endPoint, queryParam, headerParam)
                }
                AppMethod.DELETE -> {
                    apiService.delete(endPoint, bodyParam, queryParam, headerParam)
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
            val dis = ob2.subscribe({
                onSuccess?.invoke(it)
            }, {
                onError?.invoke(errorsHandling(it))
            }, {})
            disposable = dis
            return dis
        }

    }

    fun download(file:File): Disposable {

        with(appRequest) {



            var ob2 = RetrofitObject.retrofit.downloadFileUrlSync(endPoint, headerParam)
                .delay(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())

            if (observeOnMainThread ?: Constants.OBSERVER_ON_MAIN_THREAD) {
                ob2 = ob2.observeOn(AndroidSchedulers.mainThread())
            }
            val dis = ob2.subscribe({
                writeResponseBodyToDisk(it,file)
                onSuccess?.invoke(ResponseModel())
            }, {err->
                onError?.invoke(ErrorResponseModel().also { it.errorMessage = err.message ?: err.localizedMessage })
            }, {})
            disposable = dis
            return dis
        }

    }





    private fun writeResponseBodyToDisk(body: ResponseBody, file:File): Boolean  {
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

                    Log.d("mud", "file download: $fileSizeDownloaded of $fileSize")
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



}