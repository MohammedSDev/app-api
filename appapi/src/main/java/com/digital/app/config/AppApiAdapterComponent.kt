package com.digital.app.config

import java.lang.reflect.Type

data class AppApiAdapterComponent(
    val type: Type
    , val adapter: Any
)