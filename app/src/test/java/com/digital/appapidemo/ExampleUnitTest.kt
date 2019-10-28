package com.digital.appapidemo

import android.provider.SyncStateContract
import com.digital.app.api.*
import com.digital.app.config.AppUploadableFile
import com.digital.app.config.appConfig
import org.junit.Test

import org.junit.Assert.*
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun testLogin2() {
        val p = hashMapOf<String, Any>(
            "login" to "7",
            "password" to "777",
            "slug" to "bingazy"
        )



        post<CustomAppFun>()
            .preRequest {
                queryParam = p
                url = "sessions"
//                observeOnMainThread = false
//                delay = 0000L
            }
            .onSuccess {


            }
            .onError {
                if (it is MainErrorModel) {
                    println( "onError. is main Error model")

                } else {
                    println("onError. not mainError model")
                }
                println("onError,${it.errorCode}")
                println( "onError,${it.errorMessage}")
                println( "onError server mes,${(it as MainErrorModel).error}")
            }
            .call<MainResponse, MainErrorModel>()




        /*post("http://api.dev.jisr.net/v2/sessions")
            .preRequest {
                queryParam = p
//                delay = 0000L
            }
            .onSuccess {
                if (it is LoginModel) {
                    println(it.message)
                    println( it.success)
                } else {
                    println( "onsuccess, not LoginModel..$it.toString()")
                }

            }
            .onError {
                if (it is MainErrorModel) {
                    println( "onError. is main Error model")

                } else {
                    println("onError. not mainError model")
                }
                println("onError,${it.errorCode}")
                println( "onError,${it.errorMessage}")
            }
            .call<LoginModel, MainErrorModel>()*/



        Thread.sleep(700000)

    }


    @Test
    fun testDownload(){
//        appConfig {
//            BASE_URL = "http://www.goo.comg/"
//        }

        val file = File.createTempFile("pppp __  ","su kk")
        println(file.name)
        println(file.absolutePath)
        println(file)
//        val url = "https://download.quranicaudio.com/quran/abdulwadood_haneef/021.mp3"
        val url = "https://www.shell.com/energy-and-innovation/the-energy-future/scenarios/shell-scenario-sky/_jcr_content/pagePromo/image.img.960.jpeg/1548184031017/clear-blue-sky.jpeg"
        download(file, AppRequest(url).also { it.observeOnMainThread = false },{
            println("download success :)\nfile 👍 size:${file.length()}")
        },{
            println("download failed..:($it")
            println("download failed..:(${it.errorMessage}")
            println("download failed..:(${it.errorCode}")
        })

        Thread.sleep(900000)
    }


    @Test
    fun testUpload(){
        val token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoyNjEsImlhdCI6IjIwMTktMTAtMjcgMTI6NTc6MDkgVVRDIn0.4S2fgyogyPPcrU_U9XZ9lSxdMNjeFn8T8vUPweaVWTo"

        val f = File("C:\\Users\\Gg\\Pictures\\0.jpg")
        println(f.name)
        println(f.length())

//        appConfig { BASE_URL = "http://api.dev.brickspms.com/v2/" }
        put("provider/profile")
            .preRequest {
                isMultiPart = true
                this.multiPartFiles =
                    listOf(AppUploadableFile("provider[avatar]",f, "image/jpg"))
                headerParam = hashMapOf(
                    "auth-token" to token
                    ,"slug" to "default"
                )
                observeOnMainThread = false
            }
            .onSuccess {  println("onSuccess,  ")}
            .onError { println("onError, ${it.errorCode},${it.errorMessage}") }
            .call<MainResponse,MainErrorModel>()


        Thread.sleep(900000)

    }



}


/*
    @Test
    fun testCase(){
        val cz = AppApiAdapterComponent(String::class.java,TestDeseializeGson())
        Constants.BASE_URL = "http://www.google.com"
        Constants.ADAPTERS = listOf(cz)





        val c = MyApi::class.java
        RetrofitObject.getCustomRetrofit(MyApi::class.java).testGet()
    }
}


class TestDeseializeGson : JsonDeserializer<String> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): String{return ""}

    }
*/
