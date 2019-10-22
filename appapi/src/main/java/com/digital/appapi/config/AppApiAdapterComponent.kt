package com.digital.appapi.config

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

data class AppApiAdapterComponent(
    val type: Type
    , val adapter: Any
)