package com.digital.appapidemo

import com.digital.app.*
import com.digital.app.api.*
import com.digital.app.config.AppApiAdapterComponent
import com.digital.app.config.AppUploadableFile
import com.digital.app.config.Constants
import com.digital.app.config.appConfig
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import java.io.BufferedInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val THREAD_SLEEP: Long = 8000

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Before
    fun appConfig() {
        appConfig {
//            BASE_URL = "https://api.tuby.dev.clicksandbox.com/v1/mob/channels/"
            BASE_URL = "http://api.dev.jisr.net/v2/"
            OBSERVER_ON_MAIN_THREAD = false

        }
    }

    /*


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
        download(file, AppRequestParam(url).also { it.observeOnMainThread = false },{
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
            .onSuccess { it-> println("onSuccess,  ")}
            .onError {it->  println("onError, ${it.errorCode},${it.errorMessage}") }
            .onStatusChange { println(it.toString()) }
            .call<MainResponse>()


        Thread.sleep(900000)

    }


    @Test
    fun testOnError(){


        val url = "https://api-reservation-test.herokuapp.com/v1/user?locale=ar"
//        appConfig {
//            BASE_URL = "https://www.google.com/"
//            OBSERVER_ON_MAIN_THREAD = false
////            errorModel = MainErrorModel::class.java
//        }
        val request = get(url)
            .preRequest { errorModel = MainErrorModel::class.java
            handleNetworkStatus = false }
            .onSuccess { it-> println("onSuccess") }
            .onError {it,state->
                println("onError")
                println(it.javaClass)

                if(it is MainErrorModel){
                    println("error is MainErrorModel.class")
                    println(it.error)
                    state?.invoke(AppNetworkStatus.OnError(it.error,false))
                }
            }
            .onStatusChange {
                println("--------------------------onStatusChange------------------")
                val c = when(it){
                    is AppNetworkStatus.InProgress ->{}
                    is AppNetworkStatus.OnSuccess ->{}
                    is AppNetworkStatus.OnError ->{
                        println("--------------OnError--------------------")
                        println("message:${it.message}")
                        println("tag: $it.tag")
                    }
                    is AppNetworkStatus.OnCustom->{}
                    is AppNetworkStatus.OnCancel->{
                        println("--------------------------OnCancel------------------")
                    }
                }
                println(it.toString())
            }
            .call<MainResponse>()

        Thread.sleep(900000)

    }
    @Test
    fun fileFun(){
        File("").toBase64()
    }

    @Test
    fun testOnCancel(){


        val url = "https://api-reservation-test.herokuapp.com/v1/user?locale=ar"
//        appConfig {
//            BASE_URL = "https://www.google.com/"
//            OBSERVER_ON_MAIN_THREAD = false
////            errorModel = MainErrorModel::class.java
//        }
        val request = get(url)
            .preRequest { errorModel = MainErrorModel::class.java
            handleNetworkStatus = false }
            .onSuccess { it-> println("onSuccess") }
            .onError {it,state->
                println("onError")
                println(it.javaClass)

                if(it is MainErrorModel){
                    println("error is MainErrorModel.class")
                    println(it.error)
                    state?.invoke(AppNetworkStatus.OnError(it.error,false))
                }
            }
            .onStatusChange {
                println("--------------------------onStatusChange------------------")
                val c = when(it){
                    is AppNetworkStatus.InProgress ->{}
                    is AppNetworkStatus.OnSuccess ->{}
                    is AppNetworkStatus.OnError ->{
                        println("--------------OnError--------------------")
                        println("message:${it.message}")
                        println("tag: $it.tag")
                    }
                    is AppNetworkStatus.OnCustom->{}
                    is AppNetworkStatus.OnCancel->{
                        println("--------------------------OnCancel------------------")
                    }
                }
                println(it.toString())
            }
            .call<MainResponse>()

        Thread.sleep(600)
        request.cancel()
        Thread.sleep(900000)

    }
    @Test
    fun testUrlConnect(){
        val url = URL("http://api.tuby.dev.clicksandbox.com/v1/mob/channels")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"
        try {
            val `in` = BufferedInputStream(urlConnection.getInputStream())
            println("-----------in---------")
//            readStream(`in`)
        } finally {
            urlConnection.disconnect()
        }


        Thread.sleep(20000)

    }*/

    @Test
    fun testLoginError() {
        val p = hashMapOf<String, Any>(
            "login" to "7",
            "password" to "777",
            "slug" to "bingazy"
        )




        post<MainResponse, MainErrorModel, CustomAppFun>(
            CustomAppFun::class.java,
            MainResponse::class.java,
            MainErrorModel::class.java)

            .preRequest {
                queryParam = p
                url = "sessions"
//                observeOnMainThread = false
//                delay = 0000L
            }
            .onSuccess { it ->
                assertTrue(it is MainResponse)
                assertEquals(MainResponse::javaClass,it.javaClass )

            }
            .onError { it ->
               if (it is MainErrorModel) {
                    println("onError. is main Error model")

                } else {
                    println("onError. not mainError model")
                }
                println("onError,${it.errorCode}")
                println("onError,${it.errorMessage}")
                println("onError server mes,${(it as MainErrorModel).error}")
                assertTrue(it is MainErrorModel)
                println("expected type " + MainErrorModel::javaClass)
                println("actual type "+it::javaClass)
//                assertEquals(MainErrorModel::javaClass,it.javaClass )

            }
            .call()
        Thread.sleep(THREAD_SLEEP)

    }

    @Test
    fun testLoginSuccess() {
        val p = hashMapOf<String, Any>(
            "login" to "7",
            "password" to "7",
            "slug" to "bingazy"
        )




        post<MainResponse, MainErrorModel, CustomAppFun>(
            CustomAppFun::class.java,
            MainResponse::class.java,
            MainErrorModel::class.java)

            .preRequest {
                queryParam = p
                url = "sessions"
//                observeOnMainThread = false
//                delay = 0000L
            }
            .onSuccess { it ->
                println("onSuccess printing ...")
                println(it.success)

                assertTrue(it is MainResponse)

            }
            .onError { it ->
                assertTrue(it is MainErrorModel)
                assertEquals(MainErrorModel::javaClass,it.javaClass )
                if (it is MainErrorModel) {
                    println("onError. is main Error model")

                } else {
                    println("onError. not mainError model")
                }
                println("onError,${it.errorCode}")
                println("onError,${it.errorMessage}")
                println("onError server mes,${(it as MainErrorModel).error}")
            }
            .call()

        Thread.sleep(THREAD_SLEEP)

    }

    private fun <T:ResponseModel, E:ErrorResponseModel, A : AppFunctions<T,E>> post2(
        response: Class<A>,
        error: Class<T>,
        appFunction: Class<E>
    ) {


    }

    @Test
    fun testHttps() {


        val url = "https://api.tuby.dev.clicksandbox.com/v1/mob/channels"
//        Constants.BASE_URL = "https://www.google.com/"
//        Constants.OBSERVER_ON_MAIN_THREAD = false

        val request = post(url, MainResponse::class.java, MainErrorModel::class.java)
            .preRequest { observeOnMainThread = false }
            .onSuccess { it, status ->
                status?.invoke(AppNetworkStatus.OnCustom(1))
                println("\n\n\n\n\n------onSuccess-----\n\n\n\n\n")
            }
            .onError { it, status ->
                status?.invoke(AppNetworkStatus.OnError())
                println(
                    "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n--------------------onError----------\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n"
                )
                assertTrue(it is MainErrorModel)
                println(it.error)
            }
            .onSuccess { it ->
                assertTrue(it is MainResponse)
                Thread.interrupted()

            }
            .onStatusChange {
                val c = when (it) {
                    is AppNetworkStatus.InProgress -> {
                    }
                    is AppNetworkStatus.OnSuccess -> {
                    }
                    is AppNetworkStatus.OnError -> {
                    }
                    is AppNetworkStatus.OnCustom -> {
                    }
                    is AppNetworkStatus.OnCancel -> {
                    }
                }
                println("--------------------------Sattus------------------")
                println(it.toString())
            }
            .call()

        Thread.sleep(15000)

    }

    @Test
    fun testHttpsCancel() {


        val url = "https://api.tuby.dev.clicksandbox.com/v1/mob/channels"
//        Constants.BASE_URL = "https://www.google.com/"
//        Constants.OBSERVER_ON_MAIN_THREAD = false

        val request = post(url, MainResponse::class.java, MainErrorModel::class.java)
            .preRequest { observeOnMainThread = false }
            .onSuccess { it, status ->
                status?.invoke(AppNetworkStatus.OnCustom(1))
                println("\n\n\n\n\n------onSuccess-----\n\n\n\n\n")
            }
            .onError { it, status ->
                status?.invoke(AppNetworkStatus.OnError())
                println(
                    "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n--------------------onError----------\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n"
                )
                assertTrue(it is MainErrorModel)
                println(it.error)
            }
            .onSuccess { it ->
                assertTrue(it is MainResponse)
                Thread.interrupted()

            }
            .onStatusChange {
                val c = when (it) {
                    is AppNetworkStatus.InProgress -> {
                    }
                    is AppNetworkStatus.OnSuccess -> {
                    }
                    is AppNetworkStatus.OnError -> {
                    }
                    is AppNetworkStatus.OnCustom -> {
                    }
                    is AppNetworkStatus.OnCancel -> {
                    }
                }
                println("--------------------------Sattus------------------")
                println(it.toString())
            }
            .call()

        request.cancel()
        Thread.sleep(15000)

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
//        val url = "https://www.shell.com/energy-and-innovation/the-energy-future/scenarios/shell-scenario-sky/_jcr_content/pagePromo/image.img.960.jpeg/1548184031017/clear-blue-sky.jpeg"
        val url = "https://cdn1.iconfinder.com/data/icons/internet-28/48/41-512.png"
        download(file, AppRequestParam(url).also { it.observeOnMainThread = false },{
            println("download success :)\nfile 👍 size:${file.length()}")
        },{
            println("download failed..:($it")
            println("download failed..:(${it.errorMessage}")
            println("download failed..:(${it.errorCode}")
        })

        Thread.sleep(90000)
    }

    @Test
    fun testHttpsCancel2() {

        val requests = AppCompositeDisposable()
        val CHANNEL_API_REQUEST = "channel"
        val url = "https://api.tuby.dev.clicksandbox.com/v1/mob/channels"
//        Constants.BASE_URL = "https://www.google.com/"
//        Constants.OBSERVER_ON_MAIN_THREAD = false

        val request = post(url, MainResponse::class.java, MainErrorModel::class.java)
            .preRequest { observeOnMainThread = false }
            .onSuccess { it, status ->
                status?.invoke(AppNetworkStatus.OnCustom(1))
                println("\n\n\n\n\n------onSuccess-----\n\n\n\n\n")
            }
            .onError { it, status ->
                status?.invoke(AppNetworkStatus.OnError())
                println(
                    "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n--------------------onError----------\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n"
                )
                assertTrue(it is MainErrorModel)
                println(it.error)
            }
            .onSuccess { it ->
                assertTrue(it is MainResponse)
                Thread.interrupted()

            }
            .onStatusChange {
                val c = when (it) {
                    is AppNetworkStatus.InProgress -> {
                    }
                    is AppNetworkStatus.OnSuccess -> {
                    }
                    is AppNetworkStatus.OnError -> {
                    }
                    is AppNetworkStatus.OnCustom -> {
                    }
                    is AppNetworkStatus.OnCancel -> {
                    }
                }
                println("--------------------------Sattus------------------")
                println(it.toString())
            }
            .call()

        requests.add(CHANNEL_API_REQUEST,request)
        Thread.sleep(1000)
        requests.cancel(CHANNEL_API_REQUEST)
        Thread.sleep(15000)

    }

    fun testDownloadCopy(){

        val file = File.createTempFile("prefix_name","suffix")
        val fileUrl = "https://..."
        val appRequest = AppRequestParam(fileUrl)
        //appRequest.headerParam = ...
        download(file, appRequest,{
            //displayFileSize(file.length())
            //..
        },{error->
            //alertUser(error.errorMessage)
        })

        Thread.sleep(90000)
    }

    /*@Test
    fun testCopy() {


        val yourParams = AppParamMap<String,Any>()
//        val yourParams = HashMap<String,String>()
        yourParams["your_key"] = listOf("value1","value2","value3")
        yourParams["your_key"] = "your_value"
        //..
        //to override the BASE_URL, pass full url, e.g: https://bitbucket.org/...
        val request = post("repositories", MainResponse::class.java, MainErrorModel::class.java)
            .preRequest {
                bodyParam = yourParams
                queryParam = yourParams
                headerParam = yourParams
                this.delay = 2000

            }
            .onSuccess { response ->
                //do stuff.
                //e.g: setData(response)
            }
            .onError { error ->
                //do stuff
                //e.g: alertUser(error.errorMessage)
            }

            .onStatusChange {
                val c = when (it) {
                    is AppNetworkStatus.InProgress -> {
                        //myLoader.start()
                    }
                    is AppNetworkStatus.OnSuccess -> {
                        //myLoader.hide()
                    }
                    is AppNetworkStatus.OnError -> {
                        //myLoader.error()
                    }
                    is AppNetworkStatus.OnCustom -> {
                        //myLoader.stop()
                    }
                    is AppNetworkStatus.OnCancel -> {
                        //myLoader.stop()
                    }
                }
            }
            .call()


        //to cancel
        //request.cancel()


    }*/
}

@Test
fun testCase(){
//    val cz = AppApiAdapterComponent(String::class.java,TestDeseializeGson())
////    Constants.BASE_URL = "http://www.google.com"
////    Constants.ADAPTERS = listOf(cz)
//
//
//
//
//
//    val tls = MyApi::class.java
//    RetrofitObject.getCustomRetrofit(MyApi::class.java).testGet()
}
/*

}


class TestDeseializeGson : JsonDeserializer<String> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): String{return ""}

    }
*/
