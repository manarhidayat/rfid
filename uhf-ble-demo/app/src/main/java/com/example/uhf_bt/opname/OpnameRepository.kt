package com.example.uhf_bt.opname

import OpnameReport
import com.example.uhf_bt.api.ApiService
import com.example.uhf_bt.model.AssetOpname
import com.example.uhf_bt.model.BaseResponse
import com.example.uhf_bt.model.ForeignResponse
import com.example.uhf_bt.model.NextCodeResponse
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.model.OpnameLocation
import com.example.uhf_bt.model.OpnameReportPdf
import com.example.uhf_bt.model.OpnameSubmitItem
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface OpnameRepository {

    suspend fun fetchOpnameList(
        code: String?,
        startDate: String?,
        endDate: String?
    ): Response<BaseResponse<List<Opname>>>

    suspend fun addOpname(request: Map<String, String>): Response<Void>

    suspend fun fetchOpnameLocation(code: String, search: String?): Response<BaseResponse<List<OpnameLocation>>>
    suspend fun fetchOpnameListLocation(locId: String, assId: String, search: String?): Response<BaseResponse<List<AssetOpname>>>
    suspend fun fetchOpnameReport(code: String?): Response<BaseResponse<List<OpnameReport>>>
    suspend fun fetchOpnameReportPdf(code: String?): Response<BaseResponse<List<OpnameReportPdf>>>

    suspend fun updateOpnameLocation(data: @JvmSuppressWildcards List<Map<String, Any?>>): Response<Void>
    suspend fun submitOpnameListLocation(assets: List<OpnameSubmitItem>): Response<Void>
    suspend fun fetchForeignOpname(assetCode: String): Response<ForeignResponse<List<AssetOpname>>>
    suspend fun getNextCode(): Response<NextCodeResponse>
}

class OpnameRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : OpnameRepository {

    override suspend fun fetchOpnameList(
        code: String?,
        startDate: String?,
        endDate: String?
    ): Response<BaseResponse<List<Opname>>> {
        return apiService.getOpnameList(code, startDate, endDate)
    }

    override suspend fun addOpname(request: Map<String, String>): Response<Void> {
        return apiService.addOpname(request)
    }

    override suspend fun fetchOpnameLocation(code: String, search: String?) = apiService.getOpnameLocation(code, search)
    override suspend fun fetchOpnameListLocation(locId: String, assId: String, search: String?) = apiService.getOpnameListLocation(locId, assId, search)
    override suspend fun fetchOpnameReport(code: String?) = apiService.getOpnameReport(code)
    override suspend fun fetchOpnameReportPdf(code: String?) = apiService.getOpnameReportPdf(code)

    override suspend fun updateOpnameLocation( data: @JvmSuppressWildcards List<Map<String, Any?>>): Response<Void> {
        return apiService.updateOpnameLocation(data)
    }

    override suspend fun submitOpnameListLocation(assets: List<OpnameSubmitItem>): Response<Void> {
        val request = mapOf("data" to assets)
        return apiService.submitOpnameListLocation(request)
    }

    override suspend fun fetchForeignOpname(assetCode: String): Response<ForeignResponse<List<AssetOpname>>> {
        return apiService.getForeignOpname(assetCode)
    }

    override suspend fun getNextCode(): Response<NextCodeResponse> {
        return apiService.getNextCode()
    }

}