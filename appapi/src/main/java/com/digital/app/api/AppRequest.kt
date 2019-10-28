package com.digital.app.api

import com.digital.app.config.AppUploadableFile
import okhttp3.RequestBody
import java.io.File
import java.nio.file.Files


/**
 * container of request params & options
 * */
class AppRequest(var url: String){
    var headerParam: Map<String,String> = hashMapOf()
    var queryParam: Map<String,Any> = hashMapOf()
    var bodyParam: Map<String,Any> = hashMapOf()
    var delay: Long = 0//ms
    var observeOnMainThread: Boolean? = null
    var isMultiPart: Boolean = false
    var multiPartFiles: List<AppUploadableFile>? = null
    var multiBodyParam: Map<String,RequestBody> = hashMapOf()

}