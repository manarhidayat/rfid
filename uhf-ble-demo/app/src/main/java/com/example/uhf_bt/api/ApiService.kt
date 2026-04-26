package com.example.uhf_bt.api

import com.example.uhf_bt.model.AssetOpname
import com.example.uhf_bt.model.BaseResponse
import com.example.uhf_bt.model.LoginRequest
import com.example.uhf_bt.model.LoginResponse
import com.example.uhf_bt.model.MasterDataItem
import com.example.uhf_bt.model.NextCodeResponse
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.model.OpnameLocation
import com.example.uhf_bt.model.OpnameSubmitItem
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("loginApi")
    fun login(@Body loginRequest: LoginRequest?): Call<LoginResponse?>?


    @Headers("Content-Type: application/json")
    @GET("api/assets")
    fun getMasterData(
        @Header("Authorization") token: String?,
        @Query("token") queryToken: String?
    ): Call<MutableList<MasterDataItem?>?>?

    @GET("api/opname/")
    suspend fun getOpnameList(
        @Query("assro_code") code: String?,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?
    ): Response<BaseResponse<List<Opname>>>

    @POST("api/opname/store")
    suspend fun addOpname(
        @Body request: Map<String, String>
    ): Response<Void> // Atau sesuaikan dengan response API Anda

    @GET("api/opname-location/{id}")
    suspend fun getOpnameLocation(
        @Path("id") id: String, // Nama "id" di sini harus SAMA dengan {id} di atas
        @Query("search") search: String? = null
    ): Response<BaseResponse<List<OpnameLocation>>>

    @GET("api/opname-location/master/location/{id}/assets")
    suspend fun getOpnameListLocation(
        @Path("id") id: String,
        @Query("search") search: String?
    ): Response<BaseResponse<List<AssetOpname>>>

    @GET("opname/report")
    suspend fun getOpnameReport(
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
        @Query("code") code: String?
    ): Response<List<Opname>>

    @Headers("Content-Type: application/json")
    @POST("api/opname-location/store")
    suspend fun updateOpnameLocation(
        @Body request: Map<String, @JvmSuppressWildcards List<OpnameLocation>>
    ): Response<Void>

    @POST("api/opname-part/store")
    suspend fun submitOpnameListLocation(
        @Body request: Map<String, @JvmSuppressWildcards List<OpnameSubmitItem>>
    ): Response<Void>

    @GET("opname/foreign-asset")
    suspend fun getForeignOpname(
        @Query("asset_code") assetCode: String
    ): Response<AssetOpname>

    @GET("api/opname/next-code")
    suspend fun getNextCode(): Response<NextCodeResponse>
}