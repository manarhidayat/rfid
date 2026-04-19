package com.example.uhf_bt.api

import com.example.uhf_bt.model.AssetOpname
import com.example.uhf_bt.model.LoginRequest
import com.example.uhf_bt.model.LoginResponse
import com.example.uhf_bt.model.MasterDataItem
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.model.OpnameLocation
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
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

    @GET("opname/list")    suspend fun getOpnameList(
        @Query("code") code: String?,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?
    ): Response<List<Opname>>

    @POST("opname/add")
    suspend fun addOpname(
        @Body opname: Opname
    ): Response<Void> // Atau sesuaikan dengan response API Anda

    @GET("opname/location")
    suspend fun getOpnameLocation(
        @Query("code") code: String,
        @Query("search") search: String?
    ): Response<List<OpnameLocation>>

    @GET("opname/list-location")
    suspend fun getOpnameListLocation(
        @Query("opname_no") opnameNo: Int,
        @Query("search") search: String?
    ): Response<List<AssetOpname>>

    @GET("opname/report")
    suspend fun getOpnameReport(
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
        @Query("code") code: String?
    ): Response<List<Opname>>

    @FormUrlEncoded
    @POST("opname/update-location-status")
    suspend fun updateOpnameLocationStatus(
        @Field("code") code: String,
        @Field("location_id") locationId: Int,
        @Field("status") status: String
    ): Response<Void>

    @POST("opname/submit-list-location")
    suspend fun submitOpnameListLocation(
        @Body assets: List<AssetOpname>
    ): Response<Void>

    @GET("opname/foreign-asset")
    suspend fun getForeignOpname(
        @Query("asset_code") assetCode: String
    ): Response<AssetOpname>

}