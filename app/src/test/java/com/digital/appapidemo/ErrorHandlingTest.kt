package com.digital.appapidemo

import com.digital.app.ErrorResponseModel
import com.digital.app.ResponseModel
import com.digital.app.api.get
import com.digital.app.config.Constants
import org.junit.Before
import org.junit.Test

class ErrorHandlingTest {

    data class MySuccResponse(val nothing: Nothing?) : ResponseModel()
    class MyErrorModel() : ErrorResponseModel(){
        val error:String? = null
    }


    @Before
    fun setupAPiConfig() {

//        Constants.BASE_URL = "https://api-reservation-test.herokuapp.com/v1/"
//        Constants.OBSERVER_ON_MAIN_THREAD = false

    }
/*
    @Test
    fun testHeaderMissing() {
        get("user")
            .onSuccess {it->
                println("onSuccess")
                println(it)
            }
            .onError {it->
                println("onError")
                println(it)
                println("is it MyErrorModel:${it is MyErrorModel}")
            }
            .call<MySuccResponse>()


        Thread.sleep(60 * 1000)
    }*/
}