package com.digital.app.config

import java.io.File

data class AppUploadableFile(
    val fileKeyName:String
    ,val file: File
    ,val mediaType: String
)