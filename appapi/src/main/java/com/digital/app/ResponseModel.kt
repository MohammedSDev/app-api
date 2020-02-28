package com.digital.app

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
open class ResponseModel{
    var code:Int = -1
}


data class DownloadModel(
    val filePath:String
):ResponseModel()

data class DownloadProcess(val percentage:Long,val fileSize:Long)