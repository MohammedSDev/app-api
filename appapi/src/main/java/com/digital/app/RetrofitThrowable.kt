package com.digital.app


class RetrofitThrowable(val error:ErrorResponseModel, message:String):Throwable(message)