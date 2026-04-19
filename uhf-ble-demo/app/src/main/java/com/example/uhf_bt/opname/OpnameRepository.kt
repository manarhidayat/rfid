package com.example.uhf_bt.opname

import com.example.uhf_bt.api.ApiService
import com.example.uhf_bt.model.AssetOpname
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.model.OpnameLocation
import retrofit2.Response
import javax.inject.Inject

interface OpnameRepository {

    suspend fun fetchOpnameList(
        code: String?,
        startDate: String?,
        endDate: String?
    ): Response<List<Opname>>

    suspend fun addOpname(opname: Opname): Response<Void>

    suspend fun fetchOpnameLocation(code: String, search: String?): Response<List<OpnameLocation>>
    suspend fun fetchOpnameListLocation(opnameNo: Int, search: String?): Response<List<AssetOpname>>
    suspend fun fetchOpnameReport(start: String?, end: String?, code: String?): Response<List<Opname>>

    suspend fun updateOpnameLocationStatus(code: String, locationId: Int, status: String): Response<Void>
    suspend fun submitOpnameListLocation(assets: List<AssetOpname>): Response<Void>
    suspend fun fetchForeignOpname(assetCode: String): Response<AssetOpname>
}

class OpnameRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : OpnameRepository {

    override suspend fun fetchOpnameList(
        code: String?,
        startDate: String?,
        endDate: String?
    ): Response<List<Opname>> {
        return apiService.getOpnameList(code, startDate, endDate)
    }

    override suspend fun addOpname(opname: Opname): Response<Void> {
        return apiService.addOpname(opname)
    }

    override suspend fun fetchOpnameLocation(code: String, search: String?) = apiService.getOpnameLocation(code, search)
    override suspend fun fetchOpnameListLocation(opnameNo: Int, search: String?) = apiService.getOpnameListLocation(opnameNo, search)
    override suspend fun fetchOpnameReport(start: String?, end: String?, code: String?) = apiService.getOpnameReport(start, end, code)

    override suspend fun updateOpnameLocationStatus(
        code: String,
        locationId: Int,
        status: String
    ): Response<Void> {
        return apiService.updateOpnameLocationStatus(code, locationId, status)
    }

    override suspend fun submitOpnameListLocation(assets: List<AssetOpname>): Response<Void> {
        return apiService.submitOpnameListLocation(assets)
    }

    override suspend fun fetchForeignOpname(assetCode: String): Response<AssetOpname> {
        return apiService.getForeignOpname(assetCode)
    }

}