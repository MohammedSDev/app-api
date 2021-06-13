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

data class DownloadProcess(val percentage: Long, val fileSize: Long)