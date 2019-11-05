package com.digital.app

import com.digital.app.config.Constants
import com.google.gson.*
import com.jisr.ess.http.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type

//GsonBuilder().registerTypeAdapter(type,CustomDGson::class.java).create()

object RetrofitObject {


    private val logging = HttpLoggingInterceptor()
        .setLevel(Constants.DEBUG_LEVEL)

    init {
        Constants.OK_HTTP_CLIENT?.apply {
            if(!Constants.OK_HTTP_CLIENT_KEEP_PURE) {
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
    private val customGson = GsonBuilder().apply {
        Constants.ADAPTERS.forEach { item ->
            registerTypeAdapter(item.type, item.adapter)
        }
    }.create()

    private var retrofit_: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(customGson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(Constants.OK_HTTP_CLIENT?.build() ?: httpClient.build())
        .build()


    fun <Z> getCustomRetrofit(customApiInterface: Class<Z>) = retrofit_.create(customApiInterface)
    val retrofit get() = retrofit_.create(ApiInterface::class.java)

    fun <T> retrofitBodyConverter(type: Type, annotations: Array<Annotation?>): Converter<ResponseBody, T> {
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