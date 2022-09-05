package com.digital.app

import com.digital.app.config.Constants
import com.google.gson.*
import com.jisr.ess.http.ApiInterface
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import okio.GzipSource
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.*
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8

//GsonBuilder().registerTypeAdapter(type,CustomDGson::class.java).create()

object RetrofitObject {


	private val logging = HttpLoggingInterceptor()
		.setLevel(Constants.DEBUG_LEVEL)

	init {
		Constants.OK_HTTP_CLIENT?.apply {
			if (!Constants.OK_HTTP_CLIENT_KEEP_PURE) {
				addInterceptor(logging)
				connectTimeout(Constants.CONNECT_TIMEOUT, Constants.TIMEOUT_UNIT)
				readTimeout(Constants.READ_TIMEOUT, Constants.TIMEOUT_UNIT)
				writeTimeout(Constants.WRITE_TIMEOUT, Constants.TIMEOUT_UNIT)
			}
		}
	}

	private val httpClient = OkHttpClient.Builder()
		.addInterceptor(logging)
		.connectTimeout(Constants.CONNECT_TIMEOUT, Constants.TIMEOUT_UNIT)
		.readTimeout(Constants.READ_TIMEOUT, Constants.TIMEOUT_UNIT)
		.writeTimeout(Constants.WRITE_TIMEOUT, Constants.TIMEOUT_UNIT)
	// add custom gson
//    private val type = object :TypeToken<List<RequestModel>?>(){}.type
  private val customGson = (Constants.CUSTOM_GSON_CONVERTER ?: GsonBuilder()).apply {
		if (Constants.CUSTOM_GSON_CONVERTER == null || !Constants.CUSTOM_GSON_CONVERTER_KEEP_PURE) {
      Constants.ADAPTERS.forEach { item ->
        registerTypeAdapter(item.type, item.adapter)
      }
    }
	}.create()

	private val okhttpB = (Constants.OK_HTTP_CLIENT ?: httpClient)
		.also {
			if (Constants.CERTIFICATE_PINNER != null)
				it.certificatePinner(Constants.CERTIFICATE_PINNER!!)
			when (Constants.CASH_STRATEGY) {
				Constants.ON_FAILED_STRATEGY -> it.addInterceptor(OnFailedCacheInterceptor())
				Constants.FORCE_STRATEGY -> it.addInterceptor(ForceCacheInterceptor())
			}
		}

	private var retrofit_: Retrofit = Retrofit.Builder()
		.baseUrl(Constants.BASE_URL)
		.addConverterFactory(GsonConverterFactory.create(customGson))
		.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
		.client(okhttpB.build())
		.build()


	fun <Z> getCustomRetrofit(customApiInterface: Class<Z>) = retrofit_.create(customApiInterface)
	val retrofit get() = retrofit_.create(ApiInterface::class.java)

	fun <T> retrofitBodyConverter(
		type: Type,
		annotations: Array<Annotation?>
	): Converter<ResponseBody, T> {
		return retrofit_.responseBodyConverter<T>(type, annotations)
	}


	fun changeBaseUrl(baseUrl: String) {
		Constants.BASE_URL = baseUrl
		retrofit_ = Retrofit.Builder()
			.baseUrl(Constants.BASE_URL)
			.addConverterFactory(GsonConverterFactory.create(customGson))
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.client(Constants.OK_HTTP_CLIENT?.build() ?: httpClient.build())
			.build()
	}


	//https://stackoverflow.com/questions/46969104/retrofit-deserializing-list-of-objects-into-different-types
	//https://stackoverflow.com/questions/6856937/gson-custom-serializer-in-specific-case
	//https://github.com/segmentio/retrofit-jsonrpc/blob/master/jsonrpc/src/main/java/com/segment/jsonrpc/JsonRPCConverterFactory.java
	/*class RequestListDeseializeGson : JsonDeserializer<List<RequestModel>>{
			override fun deserialize(
					json: JsonElement?,
					typeOfT: Type?,
					context: JsonDeserializationContext?
			): List<RequestModel> {
					val requestsList = mutableListOf<RequestModel>()

					if(json != null){
							log(text = "json parsing start")
							val gson = Gson()
							val array = json.asJsonArray
							array.forEach { jObj->
									log(text = "json forEach parsing start")
									val obj = jObj.asJsonObject
									val requestModel = gson.fromJson(obj,RequestModel::class.java)
									//deserialize request
									when(requestModel.requestType) {
											HttpConstants.RequestType.OvertimeRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), OvertimeModel::class.java)
											}
											"AssetClearRequest" -> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), AssetClearModel::class.java)
											}
									}

									requestsList.add(requestModel)
							}
					}
					return requestsList
			}

	}*/
	/*class RequestDeseializeGson : JsonDeserializer<RequestModel>{
			override fun deserialize(
					json: JsonElement?,
					typeOfT: Type?,
					context: JsonDeserializationContext?
			): RequestModel {

					val requestModel:RequestModel
					if(json != null){
							log(text = "json parsing start")
							val gson = Gson()
									val obj = json.asJsonObject
									requestModel = gson.fromJson(obj,RequestModel::class.java)
									//deserialize request
									when(requestModel.requestType) {
											HttpConstants.RequestType.OvertimeCancellationRequest,
											HttpConstants.RequestType.OvertimeRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), OvertimeModel::class.java)
											}
											HttpConstants.RequestType.LoanCancellationRequest,
											HttpConstants.RequestType.LoanRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), LoanModel::class.java)
											}
											HttpConstants.RequestType.LetterRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), LetterModel::class.java)
											}
											HttpConstants.RequestType.BusinessTripCancellationRequest,
											HttpConstants.RequestType.BusinessTripRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), BusinessTripModel::class.java)
											}
											HttpConstants.RequestType.ExpenseClaimCancellationRequest,
											HttpConstants.RequestType.ExpenseClaimRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), ExpenceModel::class.java)
											}
//                        HttpConstants.RequestType.LeaveResumptionRequest-> {
//                            requestModel.request =
//                                gson.fromJson(obj.getAsJsonObject("request"), AnnualModel::class.java)
//                        }
											HttpConstants.RequestType.AssetCancellationRequest,
											HttpConstants.RequestType.AssetRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), AssetModel::class.java)
											}
											HttpConstants.RequestType.LeaveCancellationRequest,
											HttpConstants.RequestType.LeaveRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), LeaveModel::class.java)
											}
											HttpConstants.RequestType.ExitReEntryCancellationRequest,
											HttpConstants.RequestType.ExitReEntryRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), ExitReEntryModel::class.java)
											}
											HttpConstants.RequestType.DelegationCancellationRequest,
											HttpConstants.RequestType.DelegationRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), DelegationModel::class.java)
											}
											HttpConstants.RequestType.LeaveResumptionCancellationRequest,
											HttpConstants.RequestType.LeaveResumptionRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), ResumptionLeaveModel::class.java)
											}
											HttpConstants.RequestType.MissingPunchCancellationRequest,
											HttpConstants.RequestType.MissingPunchRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), MissingPunchModel::class.java)
											}
											HttpConstants.RequestType.ExcuseCancellationRequest,
											HttpConstants.RequestType.ExcuseRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), ExcuseModel::class.java)
											}
											HttpConstants.RequestType.HiringCancellationRequest,
											HttpConstants.RequestType.HiringRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), HiringModel::class.java)
											}
											HttpConstants.RequestType.ResignationCancellationRequest,
											HttpConstants.RequestType.ResignationRequest-> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), ResignationModel::class.java)
											}
											HttpConstants.RequestType.AssetClearCancellationRequest,
											HttpConstants.RequestType.AssetClearRequest -> {
													requestModel.request =
															gson.fromJson(obj.getAsJsonObject("request"), AssetClearModel::class.java)
											}
									}

					}else{
							requestModel = RequestModel(null,null,null,null,null,null,null,null,null)
					}
					return requestModel
			}

	}*/
}

data class ObjectWritable(
	val body: String,
	val contentType: String,
	val header: String,
	val code: Int,
	val mes: String,
	val protocol: String
) : Serializable


internal class ForceCacheInterceptor : Interceptor {
	val regex = Regex("[\"/:*?<>\\\\]")
	override fun intercept(chain: Interceptor.Chain): Response {


		val dir = Constants.CONTEXT?.get()?.cacheDir ?: return chain.proceed(chain.request())
		val fName =
			chain.request().url().toString().replace(regex, "")//.plus(System.currentTimeMillis())
		val f = File(dir, fName)
		if (f.exists()) {
			val ins = FileInputStream(f)
			val objs = ObjectInputStream(ins)
			val j = objs.readObject()
			objs.close()
			if (j is ObjectWritable) {
				println("Protocol.valueOf(j.protocol):${Protocol.valueOf(j.protocol)}")
				val fileRes = Response.Builder()
					.body(ResponseBody.create(MediaType.get(j.contentType), j.body))
					.headers(Gson().fromJson<Headers>(j.header, Headers::class.java))
					.message(j.mes)
					.protocol(Protocol.valueOf(j.protocol))
					.code(j.code)
					.request(chain.request())
					.build()


				if (fileRes is Response) {
					println("cach returned successfully for $fName")
					return fileRes
				} else {
					f.delete()
				}


			} else {
				f.delete()
				return chain.proceed(chain.request())
			}


		}

		val r = chain.proceed(chain.request())
		if (!r.isSuccessful)
			return r
		val body = r.body() ?: return r
		val charset: Charset = r.body()?.contentType()?.charset(UTF_8) ?: UTF_8

		// ---

		if (!HttpHeaders.hasBody(r)) {
			//logger.log("<-- END HTTP")
		} else if (bodyHasUnknownEncoding(r.headers())) {
			//logger.log("<-- END HTTP (encoded body omitted)")
		} else {
			val source = body.source()
			source.request(Long.MAX_VALUE) // Buffer the entire body.
			var buffer = source.buffer()

			var gzippedLength: Long? = null
			if ("gzip".equals(r.header("Content-Encoding"), ignoreCase = true)) {
				gzippedLength = buffer.size()
				GzipSource(buffer.clone()).use { gzippedResponseBody ->
					buffer = Buffer()
					buffer.writeAll(gzippedResponseBody)
				}
			}

			//val contentType = body.contentType()

			if (!isPlaintext(buffer)) {
//				logger.log("")
//				logger.log("<-- END HTTP (binary ${buffer.size}-byte body omitted)")
				return r
			}


			val fileResObj =
				ObjectWritable(
					buffer.clone().readString(charset),
					body.contentType().toString(),
					Gson().toJson(r.headers()),
					r.code(),
					r.message(),
					r.protocol().name
				)

			if (!f.exists())
				f.createNewFile()
			val objs = ObjectOutputStream(FileOutputStream(f))
			objs.writeObject(fileResObj)
			objs.flush()
			objs.close()
			println("cache save new response for file${fName}")
			return r

		}
		return r


	}

	private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
		val contentEncoding = headers.get("Content-Encoding")
		return contentEncoding != null
			&& !contentEncoding.equals("identity", true)
			&& !contentEncoding.equals("gzip", true)
	}

	private fun isPlaintext(buffer: Buffer): Boolean {
		try {
			val prefix = Buffer()
			val byteCount = if (buffer.size() < 64) buffer.size() else 64
			buffer.copyTo(prefix, 0, byteCount);

			for (i in 0..15) {
				if (prefix.exhausted()) {
					break
				}
				val codePoint = prefix.readUtf8CodePoint()
				if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
					return false
				}
			}
			return true
		} catch (e: EOFException) {
			return false // Truncated UTF-8 sequence.
		}
	}


}

internal class OnFailedCacheInterceptor : Interceptor {
	val regex = Regex("[\"/:*?<>\\\\]")
	override fun intercept(chain: Interceptor.Chain): Response {

		val dir = Constants.CONTEXT?.get()?.cacheDir ?: return chain.proceed(chain.request())
		val fName =
			chain.request().url().toString().replace(regex, "")
		val f = File(dir, fName)

		val r: Response
		try {
			r = chain.proceed(chain.request())
		} catch (x: IOException) {
			if (f.exists()) {
				val ins = FileInputStream(f)
				val objs = ObjectInputStream(ins)
				val j = objs.readObject()
				objs.close()
				if (j is ObjectWritable) {
					val fileRes = Response.Builder()
						.body(ResponseBody.create(MediaType.get(j.contentType), j.body))
						.headers(Gson().fromJson<Headers>(j.header, Headers::class.java))
						.message(j.mes)
						.protocol(Protocol.valueOf(j.protocol))
						.code(j.code)
						.request(chain.request())
						.build()


					if (fileRes is Response) {
						return fileRes
					} else {
						f.delete()
					}


				} else {
					f.delete()
					//an error occur
				}
			}
			throw x
		}
		if (!r.isSuccessful) {
			return r
		}
		val body = r.body() ?: return r
		val charset: Charset = r.body()?.contentType()?.charset(UTF_8) ?: UTF_8

		// ---

		if (!HttpHeaders.hasBody(r)) {
			//logger.log("<-- END HTTP")
		} else if (bodyHasUnknownEncoding(r.headers())) {
			//logger.log("<-- END HTTP (encoded body omitted)")
		} else {
			val source = body.source()
			source.request(Long.MAX_VALUE) // Buffer the entire body.
			var buffer = source.buffer()

			var gzippedLength: Long? = null
			if ("gzip".equals(r.header("Content-Encoding"), ignoreCase = true)) {
				gzippedLength = buffer.size()
				GzipSource(buffer.clone()).use { gzippedResponseBody ->
					buffer = Buffer()
					buffer.writeAll(gzippedResponseBody)
				}
			}

			//val contentType = body.contentType()

			if (!isPlaintext(buffer)) {
//				logger.log("")
//				logger.log("<-- END HTTP (binary ${buffer.size}-byte body omitted)")
				return r
			}


			val fileResObj =
				ObjectWritable(
					buffer.clone().readString(charset),
					body.contentType().toString(),
					Gson().toJson(r.headers()),
					r.code(),
					r.message(),
					r.protocol().name
				)

			if (!f.exists())
				f.createNewFile()
			val objs = ObjectOutputStream(FileOutputStream(f))
			objs.writeObject(fileResObj)
			objs.flush()
			objs.close()
			return r

		}
		return r


	}

	private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
		val contentEncoding = headers.get("Content-Encoding")
		return contentEncoding != null
			&& !contentEncoding.equals("identity", true)
			&& !contentEncoding.equals("gzip", true)
	}

	private fun isPlaintext(buffer: Buffer): Boolean {
		try {
			val prefix = Buffer()
			val byteCount = if (buffer.size() < 64) buffer.size() else 64
			buffer.copyTo(prefix, 0, byteCount);

			for (i in 0..15) {
				if (prefix.exhausted()) {
					break
				}
				val codePoint = prefix.readUtf8CodePoint()
				if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
					return false
				}
			}
			return true
		} catch (e: EOFException) {
			return false // Truncated UTF-8 sequence.
		}
	}


}
