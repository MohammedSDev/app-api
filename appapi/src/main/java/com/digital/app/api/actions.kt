package com.digital.app.api

import com.digital.app.*
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.internal.ws.WebSocketProtocol
import okio.ByteString
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.net.URI
import javax.net.SocketFactory

fun <T : ResponseModel, E : ErrorResponseModel> post(
    endPoint: String,
    responseModel: Class<T>,
    errorMode: Class<E>
)
        : AppFunctions<T, E> {
    val appReq = AppRequestParam(endPoint)


//    return
//    Constants.customAppFunction?.getConstructor(AppMethod::class.java,
//        AppRequestParam::class.java)?.newInstance(
//        AppMethod.POST, appReq)
//        ?:
    return AppFunctions<T, E>(AppMethod.POST, appReq).also {
        it.responseModel = responseModel
        it.errorModel = errorMode
    }
}


fun <T : ResponseModel, E : ErrorResponseModel> put(
    endPoint: String,
    responseModel: Class<T>,
    errorMode: Class<E>
): AppFunctions<T, E> {
    val appReq = AppRequestParam(endPoint)

//    return Constants.customAppFunction?.getConstructor(AppMethod::class.java,
//        AppRequestParam::class.java)?.newInstance(
//        AppMethod.PUT, appReq)
//        ?:
    return AppFunctions<T, E>(AppMethod.PUT, appReq).also {
        it.responseModel = responseModel
        it.errorModel = errorMode
    }


}

fun <T : ResponseModel, E : ErrorResponseModel> get(
    endPoint: String,
    responseModel: Class<T>,
    errorMode: Class<E>
): AppFunctions<T, E> {
    val appReq = AppRequestParam(endPoint)


//    return Constants.customAppFunction?.getConstructor(AppMethod::class.java,
//        AppRequestParam::class.java)?.newInstance(
//        AppMethod.GET, appReq)
//        ?:
    return AppFunctions<T, E>(AppMethod.GET, appReq).also {
        it.responseModel = responseModel
        it.errorModel = errorMode
    }
}

fun <T : ResponseModel, E : ErrorResponseModel> delete(
    endPoint: String,
    responseModel: Class<T>,
    errorMode: Class<E>
): AppFunctions<T, E> {
    val appReq = AppRequestParam(endPoint)

//    return Constants.customAppFunction?.getConstructor(AppMethod::class.java,
//        AppRequestParam::class.java)?.newInstance(
//        AppMethod.DELETE, appReq)
//        ?:
    return AppFunctions<T, E>(AppMethod.DELETE, appReq).also {
        it.responseModel = responseModel
        it.errorModel = errorMode
    }
}


fun <T : ResponseModel, E : ErrorResponseModel, A : AppFunctions<T, E>> post(
    customAppFunction: Class<A>,
    responseModel: Class<T>,
    errorMode: Class<E>
): A {
    val appReq = AppRequestParam("")

//    val instance = A::class.java.getDeclaredConstructor(A::class.java)
//        .newInstance()
    val instance2 =
        customAppFunction.getConstructor(AppMethod::class.java, AppRequestParam::class.java)
            .newInstance(AppMethod.POST, appReq)
    instance2.responseModel = responseModel
    instance2.errorModel = errorMode

    return instance2
}

fun <T : ResponseModel, E : ErrorResponseModel, A : AppFunctions<T, E>> get(
    customAppFunction: Class<A>,
    responseModel: Class<T>,
    errorMode: Class<E>
): A {
    val appReq = AppRequestParam("")
    val instance2 =
        customAppFunction.getConstructor(AppMethod::class.java, AppRequestParam::class.java)
            .newInstance(AppMethod.GET, appReq)
    instance2.responseModel = responseModel
    instance2.errorModel = errorMode

    return instance2
}

fun <T : ResponseModel, E : ErrorResponseModel, A : AppFunctions<T, E>> put(
    customAppFunction: Class<A>,
    responseModel: Class<T>,
    errorMode: Class<E>
): A {
    val appReq = AppRequestParam("")
    val instance2 =
        customAppFunction.getConstructor(AppMethod::class.java, AppRequestParam::class.java)
            .newInstance(AppMethod.PUT, appReq)
    instance2.responseModel = responseModel
    instance2.errorModel = errorMode

    return instance2
}

fun <T : ResponseModel, E : ErrorResponseModel, A : AppFunctions<T, E>> delete(
    customAppFunction: Class<A>,
    responseModel: Class<T>,
    errorMode: Class<E>
): A {
    val appReq = AppRequestParam("")
    val instance2 =
        customAppFunction.getConstructor(AppMethod::class.java, AppRequestParam::class.java)
            .newInstance(AppMethod.DELETE, appReq)
    instance2.responseModel = responseModel
    instance2.errorModel = errorMode

    return instance2
}

/**
 * download file
 * @param file: destination file to download into it.
 * @param isAsync: true to download in different thread.
 * @param isLargeFile: true to try download file screamingly
 *
 * you can handle download process status in InProgress status.
 * library passes nullable DownloadProcess? in tag key of InProgress Status.
 *
 * */
fun download(
    file: File,
    isAsync: Boolean,
    appRequestParam: AppRequestParam,
    isLargeFile: Boolean,
    onSuccess: (r: DownloadModel) -> Unit,
    onStatus: (s: AppNetworkStatus) -> Unit,
    onError: (e: ErrorResponseModel) -> Unit
) {

    AppFunctions<DownloadModel, ErrorResponseModel>(AppMethod.GET, appRequestParam).also {
        it.responseModel = DownloadModel::class.java
        it.errorModel = ErrorResponseModel::class.java
    }.also {
        it.isDownloadAsync = isAsync
        it.isLargeFile = isLargeFile
    }
        .onSuccess(onSuccess)
        .onError(onError)
        .onStatusChange(onStatus)
        .download(file)
}

class WebSocketActions(
    private val ws: WebSocket? = null,
    private val wsClient: WebSocketClient? = null
) {
    fun send(tx: String) {
        ws?.send(tx) ?: wsClient?.send(tx)
    }

    fun connect() {
        appApiLog("wsClient?.isOpen:${wsClient?.isOpen}")
        wsClient?.connect()
    }



}



fun webSocketOkHttp(): WebSocketActions {

    val r = Request.Builder()
//            .url("wss://echo.websocket.org")
        .url("ws://api-dev-new.sprentapp.com:8001")
        .build()
    val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            appApiLog("::onOpen")

        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            appApiLog("::onFailure")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            appApiLog("::onClosing,reason:$reason")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            appApiLog("::onMessage,text:$text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            appApiLog("::onMessage,bytes")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            appApiLog("::onClosed,reason:$reason")
        }
    }
    return WebSocketActions(
        OkHttpClient.Builder()
            .build()
//            .also {
//                it.dispatcher().executorService().shutdown()
//            }
            .newWebSocket(r, listener)

    )


}

fun webSocket(): WebSocketActions {
    val messageBuilder = MessageBuilder()
    val token = "47546.58b6766433f87baa3264658724196f69"
    val uId = "47546"
    val ws = object : WebSocketClient(URI("ws://api-dev-new.sprentapp.com:8001")) {
        override fun onOpen(handshakedata: ServerHandshake?) {
            appApiLog("onOpen,httpStatus:${handshakedata?.httpStatus}")
            appApiLog("onOpen,httpStatusMessage:${handshakedata?.httpStatusMessage}")

            //start handCheck
            val map = mapOf(
                "token" to token,
                "user_id" to uId
            )

            val handcheck = messageBuilder.getHandcheckRequestMessage(map)
            send(handcheck)
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            appApiLog("onClose,reason:$reason,remote:$remote")
        }

        var client: String = ""
        var isSubDid = false
        var map: Map<String, String> = mapOf(
            "token" to token,
            "user_id" to uId
        )

        override fun onMessage(message: String?) {
            appApiLog("onMessage:$message")
            //if handCheck success -> start connect
            val json = JSONArray(message)[0] as JSONObject
            when (json.getString("channel")) {
                MessageBuilder.HANDSHAKE_CHANNEL -> {
                    //start connect
                    client = json.getString("clientId")
                    //subscribe
                    if (!isSubDid) {
                        val sub =
                            messageBuilder.getSubscribeRequestMessage(client, "/sprenter/1425", map)
                        send(sub)
                        isSubDid = true
                    }
                    val connect = messageBuilder.getConnectRequestMessage(client, map)
                    send(connect)
                }
                MessageBuilder.CONNECT_CHANNEL -> {
                    //keep connecting
                    val connect = messageBuilder.getConnectRequestMessage(client, map,false)
                    send(connect)

                }
                MessageBuilder.SUBSCRIBE_CHANNEL -> {


                    val map = mapOf(
                        "token" to token,
                        "user_id" to uId
                    )
                    val sub =
                        messageBuilder.getCustomRequestMessage2(
                            client,
                            "new locations start update",
//                            "/sprenters/1425",
                            "/sprenter/1425",
                            map
                        )
                    send(sub)

                }
            }
            /*
            * handCheck message:
            * [{
	"channel": "/meta/handshake",
	"successful": true,
	"version": "1.0",
	"supportedConnectionTypes": ["long-polling", "cross-origin-long-polling", "callback-polling", "websocket", "eventsource", "in-process"],
	"clientId": "vkr738ayki6zhdzfs3sbhqcexg6k606",
	"advice": {
		"reconnect": "retry",
		"interval": 0,
		"timeout": 60000
	}
}]
            * */
        }

        override fun onError(ex: Exception?) {
            appApiLog("onError:${ex?.message}")
        }

    }
        .apply { connectionLostTimeout = 0 }


    return WebSocketActions(wsClient = ws)


}


internal class MessageBuilder {
    val KEY_CHANNEL = "channel"
    val KEY_VERSION = "version"
    val KEY_MIN_VERSION = "minimumVersion"
    val KEY_SUPPORT_CONNECTION_TYPES = "supportedConnectionTypes"
    val KEY_CLIENT_ID = "clientId"
    val KEY_SUBSCRIPTION = "subscription"
    val KEY_CONNECTION_TYPE = "connectionType"
    val KEY_DATA = "data"
    val KEY_EXT = "ext"
    //val KEY_ID = "id"

    //channels
    companion object {
        val HANDSHAKE_CHANNEL = "/meta/handshake"
        val CONNECT_CHANNEL = "/meta/connect"
        val DISCONNECT_CHANNEL = "/meta/disconnect"
        val SUBSCRIBE_CHANNEL = "/meta/subscribe"
        val UNSUBSCRIBE_CHANNEL = "/meta/unsubscribe"
    }

    //values
    private val VERSION_VALUE = "1.0"
    private val MIN_VERSION_VALUE = "1.0beta"
    private var mClientId: String? = null
    private var SUPPORT_CONNECTION_TYPES_VALUE: Array<String> = arrayOf("long-polling", "websocket")
    private var CONNECTION_TYPE_VALUE = "long-polling"


    fun getHandcheckRequestMessage(ext: Map<String, String>): String {
        val mes = mapOf<String, Any>(
            KEY_CHANNEL to HANDSHAKE_CHANNEL,
            KEY_VERSION to VERSION_VALUE,
            KEY_MIN_VERSION to MIN_VERSION_VALUE,
            KEY_SUPPORT_CONNECTION_TYPES to SUPPORT_CONNECTION_TYPES_VALUE
        ).plus(ext)

        val result = Gson().toJson(mes)

        appApiLog("handcheck result:$result")
        return result
    }

    fun getConnectRequestMessage(
        clientId: String,
        ext: Map<String, String>,
        wiAdvice: Boolean = false
    ): String {
        val mes = hashMapOf<String, Any>(
            KEY_CHANNEL to CONNECT_CHANNEL,
            KEY_CLIENT_ID to clientId,
            KEY_CONNECTION_TYPE to CONNECTION_TYPE_VALUE
        )
        if (wiAdvice)
            mes["advice"] = mapOf(
                "reconnect" to "handshake"
            )
        val result = Gson().toJson(mes.plus(ext))

        appApiLog("Connect result:$result")
        return result
    }

    fun getSubscribeRequestMessage(
        clientId: String,
        channel: String,
        ext: Map<String, String>
    ): String {
        val mes = mapOf<String, Any>(
            KEY_CHANNEL to SUBSCRIBE_CHANNEL,
            KEY_CLIENT_ID to clientId,
            KEY_SUBSCRIPTION to channel
        ).plus(ext)

        val result = Gson().toJson(mes)

        appApiLog("Subscribe result:$result")
        return result
    }

    fun getCustomRequestMessage(
        clientId: String,
        sendMes: String,
        channel: String,
        ext: Map<String, String>
    ): String {
        val mes = mapOf<String, Any>(
            KEY_CHANNEL to channel,
            KEY_CLIENT_ID to clientId,
            "data" to mapOf("lat" to sendMes),
            //"ext" to ext,
            "id" to ""
        ).plus(ext)

        val result = Gson().toJson(mes)

        appApiLog("Custom result:$result")
        return result
    }
    fun getCustomRequestMessage2(
        clientId: String,
        sendMes: String,
        channel: String,
        ext: Map<String, String>
    ): String {
        //val rr = "{\"channel\":\"\\/sprenters\\/47546\",\"data\":{\"lat\":24.695497512817383,\"lon\":45.68199920654297},\"clientId\":\"euvus3uuz8bogrgqdamej37c86adib0\",\"ext\":{\"token\":\"47546.58b6766433f87baa3264658724196f69\",\"user_id\":\"47546\"},\"id\":\"\"}"
        //return rr

        //\/sprenters\/47546
        val mes = mapOf<String, Any>(
            KEY_CHANNEL to channel,
            KEY_CLIENT_ID to clientId,
            "data" to mapOf("lat" to "25,1234"),
            "ext" to ext,
            "id" to ""
        )

        val result = Gson().toJson(mes)

        appApiLog("Custom result:$result")
        return result
    }
}


//refrences:
//https://cumulocity.com/guides/reference/real-time-notifications/
//https://developer.salesforce.com/docs/atlas.en-us.api_streaming.meta/api_streaming/using_streaming_api_client_connection.htm