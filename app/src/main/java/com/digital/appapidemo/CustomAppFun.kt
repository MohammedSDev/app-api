package com.digital.appapidemo

import android.content.Context
import android.widget.Toast
import com.digital.app.ErrorResponseModel
import com.digital.app.ResponseModel
import com.digital.app.api.AppFunctions
import com.digital.app.api.AppMethod
import com.digital.app.api.AppRequest

class CustomAppFun(method:AppMethod,req:AppRequest) : AppFunctions(method,req) {

    var context: Context? = null
    override fun onSuccess(block: (response: ResponseModel) -> Unit): AppFunctions {
        onSuccess = {
            if (it !is MainResponse || !it.success){
                Toast.makeText(context,"logout man!!",Toast.LENGTH_LONG).show()
            }else{
                block(it)
            }
        }
        return this
    }

    override fun onError(block: (response: ErrorResponseModel) -> Unit): AppFunctions {
        onError = block
        return this
    }



}