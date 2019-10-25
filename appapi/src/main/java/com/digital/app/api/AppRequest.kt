package com.digital.app.api

import com.digital.app.config.AppUploadableFile
import okhttp3.RequestBody
import java.io.File
import java.nio.file.Files


/**
 * container of request params & options
 * */
class AppRequest(var endPoint: String){
    var headerParam: HashMap<String,String> = hashMapOf()
    var queryParam: HashMap<String,Any> = hashMapOf()
    var bodyParam: HashMap<String,Any> = hashMapOf()
    var delay: Long = 0//ms
    var observeOnMainThread: Boolean? = null
    var isMultiPart: Boolean = false
    var multiPartFiles: List<AppUploadableFile>? = null
    var multiBodyParam: HashMap<String,RequestBody> = hashMapOf()

}