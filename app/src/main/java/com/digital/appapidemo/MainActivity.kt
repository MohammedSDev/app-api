package com.digital.appapidemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.digital.app.*
import com.digital.app.api.*
import com.digital.app.config.AppUploadableFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.net.URI

class MainActivity : AppCompatActivity() {

	class MainResponseModel : ResponseModel() {

	}
	lateinit var ws: AppWSMessagingActions
	val token = "47546.58b6766433f87baa3264658724196f69"
	val uId = "47546"
	val map = mapOf(
		"token" to token,
		"user_id" to uId
	)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		ws = webSocketMessaging(URI("ws://"), listener = object : AppMessagingEvents {
			override fun onConnect() {
				println("onConnect")
				ws.subscribe("/users/200")
			}

			override fun onDisConnect() {
				println("onDisConnect")
			}

			override fun onSubscribe(channel: String, status: Boolean) {
				println("onSubscribe: $channel")
			}

			override fun onUnSubscribe(channel: String, status: Boolean) {
				println("onUnSubscribe: $channel")

			}

			override fun onMessage(channel: String, status: Boolean, text: String) {
				println("onMessage: $channel")
			}

			override fun onError(ex: Exception?) {
				println("onError:${ex?.message}")
			}
		})
		tv.setOnClickListener {
			ws.connect(map)
		}
		tv2.setOnClickListener {
			val d = mapOf("data" to mapOf("lat" to 10.20f,"lon" to 11.2231f)
			,"ext" to map)

			ws.send("/users/200",d)
		}
		tv3.setOnClickListener {
//			wsT.connect()


		}
	}


	fun multiPartTest() {

		val kValue = "name" toRequest "Mohammed"

		File("").toBase64()
		val request = post("users", MainResponseModel::class.java, MainErrorModel::class.java)
			.preRequest {
				//                errorModel = MainErrorModel::class.java
				isMultiPart = true
				multiBodyParam = hashMapOf("name" to createRequestPart("mohammed"))
				multiPartFiles = listOf(
					AppUploadableFile(
						"fName"
						, File.createTempFile("", "")
						, getMimeType("") ?: "*/*"
					)
				)
			}
			.onSuccess { it ->

			}
			.onError { it ->

			}
			.call()


		val cc = MainErrorModel::class.java

		AppCompositeDisposable()
//            .apply {
//            add("list_req",request)
////            get("list_req")?.cancel()
////            cancel("list_req")
//            println(get("list_req"))
//        }

	}

	val comD = AppCompositeDisposable()


	override fun onDestroy() {
		super.onDestroy()
//        comD.dispose()
		comD.cancelAll()
	}
}

