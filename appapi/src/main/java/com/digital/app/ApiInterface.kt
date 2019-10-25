package com.jisr.ess.http

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.lang.StringBuilder
import retrofit2.http.Url
import retrofit2.http.GET


interface ApiInterface {

    @GET
    fun get(
        @Url endPoint: String,
        @QueryMap params: Map<String, @JvmSuppressWildcards Any>,
        @HeaderMap headerMap: Map<String, String> = mapOf()
    ): Observable<Response<ResponseBody>>


    @PUT
    fun put(
        @Url endPoint: String,
        @Body body: Any,
        @QueryMap qParams: Map<String, @JvmSuppressWildcards Any> = mapOf(),
        @HeaderMap headerMap: Map<String, String> = mapOf()
    ): Observable<Response<ResponseBody>>


    @POST
    fun post(
        @Url endPoint: String,
        @Body body: Any,
        @QueryMap qParams: Map<String, @JvmSuppressWildcards Any> = mapOf(),
        @HeaderMap headerMap: Map<String, String> = mapOf()
    ): Observable<Response<ResponseBody>>


    @DELETE
    fun delete(
        @Url endPoint: String,
        @Body body: Any,
        @QueryMap qParams: Map<String, @JvmSuppressWildcards Any> = mapOf(),
        @HeaderMap headerMap: Map<String, String> = mapOf()
    ): Observable<Response<ResponseBody>>


    @Multipart
    @POST
    fun postMultiPart(
        @Url endPoint: String
        , @PartMap params: HashMap<String, @JvmSuppressWildcards RequestBody> = hashMapOf()
        , @Part files: List<MultipartBody.Part>
        , @QueryMap qParams: HashMap<String,@JvmSuppressWildcards  Any> = hashMapOf()
        , @HeaderMap headerMap: Map<String, String> = mapOf()
    ): Observable<Response<ResponseBody>>


    @Multipart
    @PUT
    fun putMultiPart(
        @Url endPoint: String
        , @PartMap params: HashMap<String, @JvmSuppressWildcards RequestBody> = hashMapOf()
        , @Part files: List<MultipartBody.Part>
        , @QueryMap qParams: HashMap<String,@JvmSuppressWildcards  Any> = hashMapOf()
        , @HeaderMap headerMap: Map<String, String> = mapOf()
    ): Observable<Response<ResponseBody>>


    @GET
    fun downloadFileUrlSync(
        @Url fileUrl: String
        , @HeaderMap headerMap: Map<String, String> = mapOf()
    ): Observable<ResponseBody>

}