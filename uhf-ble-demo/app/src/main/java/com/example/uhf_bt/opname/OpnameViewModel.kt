package com.example.uhf_bt.opnameimport

import OpnameReport
import androidx.lifecycle.LiveData
import com.example.uhf_bt.opname.OpnameRepository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uhf_bt.model.AssetOpname
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.model.OpnameLocation
import com.example.uhf_bt.model.OpnameReportPdf
import com.example.uhf_bt.model.OpnameSubmitItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpnameViewModel @Inject constructor(
    private val repository: OpnameRepository
) : ViewModel() {

    // Opname Data
    private val _opnameList = MutableLiveData<List<Opname>>()
    val opnameList: LiveData<List<Opname>> = _opnameList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Opname Add
    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    // LiveData tambahan
    private val _locationList = MutableLiveData<List<OpnameLocation>>()
    val locationList: LiveData<List<OpnameLocation>> = _locationList

    private val _assetList = MutableLiveData<List<AssetOpname>>()
    val assetList: LiveData<List<AssetOpname>> = _assetList

    private val _foreignAsset = MutableLiveData<List<AssetOpname>>()
    val foreignAsset: LiveData<List<AssetOpname>> = _foreignAsset

    private val _nextCode = MutableLiveData<String>()
    val nextCode: LiveData<String> get() = _nextCode

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> get() = _updateStatus

    private val _opnameReport = MutableLiveData<List<OpnameReport>>()
    val opnameReport: LiveData<List<OpnameReport>> get() = _opnameReport

    private val _opnameReportPdf = MutableLiveData<List<OpnameReportPdf>>()
    val opnameReportPdf: LiveData<List<OpnameReportPdf>> get() = _opnameReportPdf

    fun getOpnameData(code: String? = null, startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.fetchOpnameList(code, startDate, endDate)
                if (response.isSuccessful) {
                    _opnameList.value = response.body()?.data ?: emptyList()
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown Error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addOpname(code: String, date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Buat object Opname (id dikosongkan jika auto-increment dari server)
                val newOpname = mapOf(
                    "assro_code" to code,
                    "assro_start_date" to date
                )
                val response = repository.addOpname(newOpname)

                if (response.isSuccessful) {
                    _isSuccess.value = true
                } else {
                    _errorMessage.value = "Gagal menyimpan data"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi fetch untuk masing-masing activity
    fun getOpnameLocation(code: String, search: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.fetchOpnameLocation(code, search)
            if (response.isSuccessful) _locationList.value = response.body()?.data ?: emptyList()
            _isLoading.value = false
        }
    }

    fun getOpnameListLocation(locId: String, assId: String, search: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.fetchOpnameListLocation(locId, assId, search)
            if (response.isSuccessful) _assetList.value = response.body()?.data ?: emptyList()
            _isLoading.value = false
        }
    }

    fun getOpnameReport(code: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.fetchOpnameReport(code)
            if (response.isSuccessful) _opnameReport.value = response.body()?.data ?: emptyList()
            _isLoading.value = false
        }
    }

    fun saveLocations(list: List<Map<String, Any?>>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = repository.updateOpnameLocation(list)
                if (response.isSuccessful) {
                    _updateStatus.postValue(true)
                } else {
                    _updateStatus.postValue(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _updateStatus.postValue(false)
                _isLoading.value = false
            }
        }
    }

    fun submitOpnameListLocation(
        assets: List<AssetOpname>,
        assroOid: String,
        assrolOid: String
    ) {
        viewModelScope.launch {
            try {
                // Mapping dari AssetOpname ke OpnameSubmitItem
                val submitItems = assets
                    .filter { !it.assId.isNullOrBlank() }
                    .map { asset ->
                    OpnameSubmitItem(
                        assroOid = assroOid,
                        assrolOid = assrolOid,
                        assId = asset.assId,
                        // "1" jika status Found/Scanned, sesuaikan dengan logic status Anda
                        status = asset.status ?: "0"
                    )
                }

                val response = repository.submitOpnameListLocation(submitItems)
                if (response.isSuccessful) {
                    _isSuccess.postValue(true)
                } else {
                    _errorMessage.postValue("Submit failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
            }
        }
    }

    fun getForeignOpname(assetCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.fetchForeignOpname(assetCode)
                if (response.isSuccessful && response.body() != null) {
                    _foreignAsset.value = response.body()?.data
                } else {
                    _errorMessage.value = "Asset tidak ditemukan"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchNextCode() {
        viewModelScope.launch {
            try {
                val response = repository.getNextCode()
                if (response.isSuccessful && response.body()?.status == 200) {
                    _nextCode.value = response.body()?.assroCode
                } else {
                    _error.value = "Gagal mengambil kode: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Terjadi kesalahan: ${e.message}"
            }
        }
    }

    fun getOpnameReportPdf(code: String?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.fetchOpnameReportPdf(code)

                if (response.isSuccessful) _opnameReportPdf.value = response.body()?.data ?: emptyList()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}