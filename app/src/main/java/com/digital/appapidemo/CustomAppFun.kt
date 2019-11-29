package com.digital.appapidemo

import android.content.Context
import android.widget.Toast
import com.digital.app.AppNetworkStatus
import com.digital.app.ErrorResponseModel
import com.digital.app.ResponseModel
import com.digital.app.api.AppFunctions
import com.digital.app.api.AppMethod
import com.digital.app.api.AppRequestParam

class CustomAppFun(method:AppMethod,req:AppRequestParam)
    : AppFunctions<MainResponse,MainErrorModel>(method,req) {

    var context: Context? = null
    override fun onSuccess(block: (response: MainResponse, status: ((state: AppNetworkStatus) -> Unit)?) -> Unit): AppFunctions<MainResponse, MainErrorModel> {
//        return super.onSuccess(block)
        println(this.javaClass.simpleName + "onSuccess.status called")

        onSuccessStatus = {response,state ->
            if (!response.success){
//                Toast.makeText(context,"logout man!!",Toast.LENGTH_LONG).show()
                println("LogOut man.. this is not a jock.")
            }else{
                block(response,state)
            }
        }
        return this


    }

    override fun onSuccess(block: (response: MainResponse) -> Unit): AppFunctions<MainResponse, MainErrorModel> {
        println(this.javaClass.simpleName + "onSuccess called")

        return super.onSuccess(block)
    }
    override fun onError(block: (response: MainErrorModel) -> Unit): AppFunctions<MainResponse, MainErrorModel> {
        println(this.javaClass.simpleName + "onError called")
//        return super.onError(block)
        onError = {
            println(this.javaClass.simpleName  + " handling onError....")
            block(it)
        }
        return  this
    }

    override fun onError(block: (response: MainErrorModel, status: ((state: AppNetworkStatus) -> Unit)?) -> Unit): AppFunctions<MainResponse, MainErrorModel> {
        println(this.javaClass.simpleName + "onError.status called")
        return super.onError(block)
    }

    /*override fun onError(block: (response: ErrorResponseModel) -> Unit): AppFunctions {
        onError = block
        return this
    }*/
}