package com.digital.app.api

import android.os.Handler
import android.os.Looper
import com.digital.app.appApiLog
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI

/**
 * Okhttp webSocket
 * */
fun webSocket(
	url: String,
	headers: Map<String, String> = emptyMap(),
	listener: WebSocketListener? = null
): WebSocket {
	val r = Request.Builder().url(url).headers(Headers.of(headers)).build()
	return OkHttpClient().newWebSocket(r, object : WebSocketListener() {
		val handler = Handler(Looper.getMainLooper())
		override fun onOpen(webSocket: WebSocket, response: Response) {
			handler.post { listener?.onOpen(webSocket, response) }
		}

		override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
			handler.post { listener?.onFailure(webSocket, t, response) }

		}

		override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
			handler.post { listener?.onClosing(webSocket, code, reason) }
		}

		override fun onMessage(webSocket: WebSocket, text: String) {
			handler.post { listener?.onMessage(webSocket, text) }

		}

		override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
			handler.post { listener?.onMessage(webSocket, bytes) }
		}

		override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
			handler.post { listener?.onClosed(webSocket, code, reason) }
		}
	})

}

/**
 * app web socket messaging
 * */
fun webSocketMessaging(
	uri: URI,
	globalExt: Map<String, String> = emptyMap(),
	messageBuilderI: AppMessageBuilderI? = null,
	listener: AppMessagingEvents? = null
): AppWSMessagingActions {
	val mb = messageBuilderI ?: AppMessageBuilder()
	mb.globalExt = globalExt

	val ws = AppWSClient(uri, mb, listener)
		.apply { connectionLostTimeout = 0 }


	return AppWSMessagingActions(ws)


}

///classes

class AppWSMessagingActions(private val messagingClient: AppMessagingActionsI) {

	val isOpen get() = messagingClient.opened
	val isClose get() = messagingClient.closed
	val channels get() = messagingClient.channels


	fun send(channel: String, data: Map<String, Any>) {
		messagingClient.send(channel, data)


	}

	/**
	 *  @param ext: hash of your key-value required by server's handcheck(e.g: token)
	 * */
	fun connect(ext: Map<String, String> = emptyMap()) {
		messagingClient?.connect(ext)
	}

	/**
	 *  @param ext: hash of your key-value required by server's handcheck(e.g: token)
	 * */
	fun subscribe(channel: String, ext: Map<String, Any> = emptyMap()) {
		messagingClient?.subscribe(channel, ext)
	}

	fun unSubscribe(channel: String, ext: Map<String, Any> = emptyMap()) {
		messagingClient.unsubscribe(channel, ext)
	}

	fun disConnect(ext: Map<String, String> = emptyMap()) {
		messagingClient.disConnect(ext)
	}


	fun addHeader(headers: Map<String, String>) {
		messagingClient.addHeaders(headers)
	}

	fun removeHeader(key: String) {
		messagingClient.removeHeaderByKey(key)
	}

	fun clearHeader() {
		messagingClient.clearHeader()
	}


}

internal class AppWSClient(
	uri: URI,
	private val messageBuilderI: AppMessageBuilderI,
	private val listener: AppMessagingEvents?
) : WebSocketClient(uri), AppMessagingActionsI {
	var clientID: String = ""
		private set
	private var handcheckExt: Map<String, String> = emptyMap()
	private val map: Map<String, String> = emptyMap()
	private val subscribeChannels = mutableSetOf<String>()

	override fun onOpen(handshakedata: ServerHandshake?) {
		appApiLog("onOpen,httpStatus:${handshakedata?.httpStatus}")
		appApiLog("onOpen,httpStatusMessage:${handshakedata?.httpStatusMessage}")

		//start handCheck

		val handcheck = messageBuilderI.getHandcheckRequestMessage(handcheckExt)
		send(handcheck)
	}

	override fun onClose(code: Int, reason: String?, remote: Boolean) {
		appApiLog("onClose,reason:$reason,remote:$remote")
	}

	override fun onMessage(message: String?) {
		appApiLog("onMessage:$message")
		//if handCheck success -> start connect
		val json = JSONArray(message)[0] as JSONObject
		when (json.getString("channel")) {
			AppMessageBuilder.HANDSHAKE_CHANNEL -> {
				//set clientId
				clientID = json.getString("clientId")
				listener?.onConnect()
				val connect = messageBuilderI.getConnectRequestMessage(clientID, map)
				send(connect)
			}
			AppMessageBuilder.CONNECT_CHANNEL -> {
				//keep connecting
				val connect = messageBuilderI.getConnectRequestMessage(clientID, map, false)
				send(connect)

			}
			AppMessageBuilder.DISCONNECT_CHANNEL -> {
				//keep connecting
				close()
				listener?.onDisConnect()

			}
			AppMessageBuilder.SUBSCRIBE_CHANNEL -> {

				val channel = json.getString("subscription")
				val status = json.getBoolean("successful")
				subscribeChannels .add(channel)
				listener?.onSubscribe(channel, status)

			}
			AppMessageBuilder.UNSUBSCRIBE_CHANNEL -> {

				val channel = json.getString("subscription")
				val status = json.getBoolean("successful")
				subscribeChannels .remove(channel)
				listener?.onUnSubscribe(channel, status)

			}
			else ->{
				val channel = runCatching { json.getString("subscription") }.getOrElse { "" }
				val status = runCatching { json.getBoolean("successful") }.getOrElse { false}
				listener?.onMessage(channel,status,message ?: "")
			}
		}

	}

	override fun onError(ex: Exception?) {
		appApiLog("onError:${ex?.message}")
		listener?.onError(ex)
	}

	//Custom actions

	override val opened: Boolean get() = isOpen
	override val closed: Boolean get() = isClosed
	override val channels: Set<String>
		get() = subscribeChannels
	override fun addHeaders(headers: Map<String, String>) {
		headers.forEach {
			addHeader(it.key, it.value)
		}
	}

	override fun removeHeaderByKey(key: String) {
		super.removeHeader(key)
	}

	override fun clearHeader() {
		clearHeaders()
	}

	override fun connect(ext: Map<String, String>) {
		handcheckExt = ext
		//start connect
		connect()
	}

	override fun disConnect(ext: Map<String, String>) {
		//diConnect
		val sub =
			messageBuilderI.getDisConnectRequestMessage(clientID, ext)
		send(sub)

	}

	/**
	 * @param channel: channel name to subscribe into it. e.g: /requests/1200
	 * */
	override fun subscribe(channel: String, ext: Map<String, Any>) {
		val sub =
			messageBuilderI.getSubscribeRequestMessage(clientID, channel, ext)
		send(sub)
	}

	override fun unsubscribe(channel: String, ext: Map<String, Any>) {
		val sub =
			messageBuilderI.getUnSubscribeRequestMessage(clientID, channel, ext)
		send(sub)
	}

	override fun send(channel: String, body: Map<String, Any>) {
		val mes = messageBuilderI.getPublishRequestMessage(
			clientID,
			channel,
			body
		)
		send(mes)
	}

	override fun subscribedChannels() = channels.toSet()
}

internal class AppMessageBuilder : AppMessageBuilderI {
	override var globalExt: Map<String, String> = emptyMap()

	val KEY_SUCCESSFUL = "successful"
	val KEY_CHANNEL = "channel"
	val KEY_VERSION = "version"
	val KEY_MIN_VERSION = "minimumVersion"
	val KEY_SUPPORT_CONNECTION_TYPES = "supportedConnectionTypes"
	val KEY_CLIENT_ID = "clientId"
	val KEY_SUBSCRIPTION = "subscription"
	val KEY_CONNECTION_TYPE = "connectionType"
	val KEY_EXT = "ext"
	val KEY_DATA = "data"
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

	/**
	 *  @param ext: hash of your key-value required by server handcheck(e.g: token)
	 * */
	override fun getHandcheckRequestMessage(ext: Map<String, String>): String {
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

	override fun getConnectRequestMessage(
		clientId: String,
		ext: Map<String, String>,
		wiAdvice: Boolean
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

	override fun getDisConnectRequestMessage(
		clientId: String,
		ext: Map<String, String>
	): String {
		val mes = hashMapOf<String, Any>(
			KEY_CHANNEL to DISCONNECT_CHANNEL,
			KEY_CLIENT_ID to clientId
		)
		val result = Gson().toJson(mes.plus(ext))

		appApiLog("Connect result:$result")
		return result
	}

	override fun getSubscribeRequestMessage(
		clientId: String,
		channel: String,
		ext: Map<String, Any>
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

	override fun getUnSubscribeRequestMessage(
		clientId: String,
		channel: String,
		ext: Map<String, Any>
	): String {
		val mes = mapOf<String, Any>(
			KEY_CHANNEL to UNSUBSCRIBE_CHANNEL,
			KEY_CLIENT_ID to clientId,
			KEY_SUBSCRIPTION to channel
		).plus(ext)

		val result = Gson().toJson(mes)

		appApiLog("Subscribe result:$result")
		return result
	}

	override fun getPublishRequestMessage(
		clientId: String,
		channel: String,
		data: Map<String, Any>
	): String {
		val mes = mapOf<String, Any>(
			KEY_CHANNEL to channel,
			KEY_CLIENT_ID to clientId,
			"ext" to globalExt
			//"id" to ""
		).plus(data)

		val result = Gson().toJson(mes)

		appApiLog("Custom result:$result")
		return result
	}
}


///interfaces

interface AppMessagingEvents {

	fun onConnect()
	fun onDisConnect()
	fun onSubscribe(channel: String, status: Boolean)
	fun onUnSubscribe(channel: String, status: Boolean)
	fun onMessage(channel: String, status: Boolean,text:String)
	fun onError(ex: Exception?)
}

interface AppMessagingActionsI {

	val opened: Boolean
	val closed: Boolean
	val channels: Set<String>
	fun connect(ext: Map<String, String>)
	fun disConnect(ext: Map<String, String>)
	fun subscribe(channel: String, ext: Map<String, Any>)
	fun unsubscribe(channel: String, ext: Map<String, Any>)
	fun send(channel: String, body: Map<String, Any>)
	fun addHeaders(headers: Map<String, String>)
	fun removeHeaderByKey(key: String)
	fun clearHeader()
	fun subscribedChannels(): Set<String>

}

interface AppMessageBuilderI {
	var globalExt: Map<String, String>

	/**
	 *  @param ext: hash of your key-value required by server handcheck(e.g: token)
	 * */
	fun getHandcheckRequestMessage(ext: Map<String, String>): String

	fun getConnectRequestMessage(
		clientId: String,
		ext: Map<String, String>,
		wiAdvice: Boolean = false
	): String

	fun getDisConnectRequestMessage(
		clientId: String,
		ext: Map<String, String>
	): String

	fun getSubscribeRequestMessage(
		clientId: String,
		channel: String,
		ext: Map<String, Any>
	): String

	fun getUnSubscribeRequestMessage(
		clientId: String,
		channel: String,
		ext: Map<String, Any>
	): String

	fun getPublishRequestMessage(
		clientId: String,
		channel: String,
		data: Map<String, Any>
	): String

}
