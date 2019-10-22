package com.digital.appapi


class RetrofitThrowable(val error:ErrorResponseModel, message:String):Throwable(message)