package com.digital.app

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
interface ResponseModel {
  var code: Int
}


data class DownloadModel(
  val filePath: String

) : ResponseModel {
  private var c = 0
  override var code: Int
    get() = c
    set(value) {
      c = value
    }
}

data class DownloadError(
  override var message: String,
  override var throwable: Throwable?,
  override var code: Int
):ErrorResponseModel{
  constructor():this("",null,-1)

}
data class DownloadProcess(val percentage: Long, val fileSize: Long)