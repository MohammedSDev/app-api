package com.digital.app.api

import com.digital.app.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

enum class AppMethod{
    POST,GET,PUT,DELETE
}

class  AppFunctions(val method:AppMethod,val appRequest:AppRequest){


    var onSuccess:((response:ResponseModel)->Unit)? = null
        private set
    var onError:((response:ErrorResponseModel)->Unit)? = null
        private set
    var disposable:Disposable? = null


    fun preRequest(d:AppRequest.()->Unit):AppFunctions{
        d(appRequest)
        return this
    }
    fun onSuccess(d:(response:ResponseModel)->Unit) :AppFunctions{
        onSuccess = d
        return this
    }
    fun onError(d:(response:ErrorResponseModel)->Unit) :AppFunctions{
        onError = d
        return this
    }
    fun cancel() {
        disposable?.dispose()
    }



     inline fun <reified  R : ResponseModel,reified E: ErrorResponseModel>call():Disposable {

        with(appRequest){

            val apiService = RetrofitObject.retrofit
            val ob = when(method){
                    AppMethod.POST ->{apiService.post(endPoint,bodyParam,queryParam,headerParam)}
                    AppMethod.PUT ->{apiService.put(endPoint,bodyParam,queryParam,headerParam)}
                    AppMethod.GET ->{apiService.get(endPoint,queryParam,headerParam)}
                    AppMethod.DELETE ->{apiService.delete(endPoint,bodyParam,queryParam,headerParam)}
                }
             val dis  = ob
                .delay(delay,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .map {
                    println()
                    println()
                    println()
                    println()
                    println("---------------------------------------------------mapOnAPpFunction")
                    handleDataPacing2<R,E>(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onSuccess?.invoke(it)
                },{
                    println()
                    println()
                    println()
                    println()
                    println("---------------------------------------------------errorOnAPpFunction")
                    println(it.message)
                    val e = errorsHandling(it)
                    println(e::class.java)
                    println(e.errorMessage)
                    println(e.errorCode)

                    onError?.invoke(
                        errorsHandling(it)
                    )
                },{})
            disposable = dis
            return dis
        }

    }

}