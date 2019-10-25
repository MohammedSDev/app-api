package com.digital.app.config

import java.io.File

data class AppUploadableFile(
    val fileName:String
    ,val file: File
    ,val mediaType: String
)