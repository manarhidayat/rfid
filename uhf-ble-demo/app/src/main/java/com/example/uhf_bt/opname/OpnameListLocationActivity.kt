package com.example.uhf_bt.opname

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.AssetOpname
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.model.OpnameLocation
import com.example.uhf_bt.opnameimport.OpnameViewModel
import com.rscja.deviceapi.RFIDWithUHFBLE
import com.rscja.deviceapi.entity.UHFTAGInfo
import com.rscja.deviceapi.interfaces.ConnectionStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OpnameListLocationActivity : AppCompatActivity() {

    private lateinit var tvCode: TextView
    private lateinit var tvLocation: TextView
    private lateinit var rvAsset: RecyclerView
    private lateinit var btnSubmit: Button
    private var assetList = mutableListOf<AssetOpname>()

    private val viewModel: OpnameViewModel by viewModels()

    private lateinit var progressBar: ProgressBar // Tambahkan ini

    private var isScanning = false
    private lateinit var btInventory: Button
    private lateinit var btStop: Button
    private lateinit var inventoryLoop: Button
    private lateinit var btnCheckForeign: Button

    private var mReader: RFIDWithUHFBLE? = null

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // Anggap FLAG_UHFINFO adalah 3 atau FLAG_UHFINFO_LIST adalah 5
            when (msg.what) {
                3 -> { // FLAG_UHFINFO (Single Tag)
                    val info = msg.obj as UHFTAGInfo
                    addEPCToList(info.epc)
                }
                5 -> { // FLAG_UHFINFO_LIST (Multiple Tags)
                    val list = msg.obj as List<UHFTAGInfo>
                    list.forEach { addEPCToList(it.epc) }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list_location)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Opname Asset"
        }

        // Inisialisasi View
        tvCode = findViewById(R.id.tv_list_opname_code)
        tvLocation = findViewById(R.id.tv_list_location_name)
        rvAsset = findViewById(R.id.rv_asset_list)
        btnSubmit = findViewById(R.id.btn_submit_opname)
        progressBar = findViewById(R.id.progressBar)
        inventoryLoop = findViewById(R.id.InventoryLoop)
        btInventory = findViewById(R.id.btInventory)
        btStop = findViewById(R.id.btStop)
        btStop.isEnabled = false

        inventoryLoop.setOnClickListener { startInventory(true) }
        btInventory.setOnClickListener { startInventory(false) }
        btStop.setOnClickListener { stopInventory() }

        try {
            mReader = RFIDWithUHFBLE.getInstance()
        } catch (e: Exception) {
            Toast.makeText(this, "RFID Init Failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // 1. Tangkap Data dari Intent (Bisa dari OpnameData atau OpnameLocation)
        val opnameData = intent.getSerializableExtra("EXTRA_OPNAME") as? Opname
        val locationData = intent.getSerializableExtra("EXTRA_LOCATION") as? OpnameLocation

        tvCode.text = "Stock Opname: ${opnameData?.assroCode ?: "-"}"
        tvLocation.text = "Location: ${locationData?.locDesc ?: "-"}"

        // 3. Setup RecyclerView
        rvAsset.layoutManager = LinearLayoutManager(this)
        rvAsset.adapter = OpnameListLocationAdapter(assetList)

        // 4. Submit Button
        btnSubmit.setOnClickListener {
            Toast.makeText(this, "Data Submitted Successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Gunakan opnameData?.no (asumsi property .no ada di model Opname)
        locationData?.let { viewModel.getOpnameListLocation(it.assrolLocId ?: "", opnameData?.assroOid ?: "") }

        findViewById<Button>(R.id.btn_search).setOnClickListener {
            val query = findViewById<EditText>(R.id.et_search_asset).text.toString().trim()
            opnameData?.let { data -> viewModel.getOpnameListLocation(locationData?.assrolLocId ?: "", opnameData?.assroOid ?: "", query) }
        }

        // Update listener button submit
        btnSubmit.setOnClickListener {
            val assroOid = opnameData?.assroOid // Gunakan OID, bukan Code jika API minta UUID
            val assrolOid = locationData?.assrolOid

            if (assetList.isNotEmpty() && assroOid != null && assrolOid != null) {
                viewModel.submitOpnameListLocation(assetList, assroOid, assrolOid)
            } else {
                if (assetList.isEmpty()) {
                    Toast.makeText(this, "No data to submit", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Missing Opname/Location ID", Toast.LENGTH_SHORT).show()
                }
            }

        }

        // Di dalam onCreate
        btnCheckForeign = findViewById<Button>(R.id.btnCheckForeign)

        btnCheckForeign.setOnClickListener {
            // 1. Ambil assetList yang statusnya "3"
            // 2. Ambil field assRfidCode-nya saja
            // 3. Gabungkan dengan koma
            val foreignRfidCodes = assetList
                .filter { it.status == "3" && !it.assRfidCode.isNullOrEmpty() }
                .map { it.assRfidCode }
                .joinToString(",")

            if (foreignRfidCodes.isNotEmpty()) {
                getForeignOpname(foreignRfidCodes)
            } else {
                Toast.makeText(this, "Tidak ada data Foreign Asset (status 3) untuk dicek", Toast.LENGTH_SHORT).show()
            }
        }

        observe()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        stopInventory()
//        mReader?.free() // Mematikan power RFID saat activity tidak terlihat
    }

    private fun startInventory(isLoop: Boolean) {
        if (mReader?.getConnectStatus() != ConnectionStatus.CONNECTED) {
            Toast.makeText(this, "Reader not connected", Toast.LENGTH_SHORT).show()
            return
        }

        isScanning = true
        btStop.isEnabled = true
        inventoryLoop.isEnabled = false
        btInventory.isEnabled = false
        btnSubmit.isEnabled = false
        btnCheckForeign.isEnabled = false

        if (isLoop) {
            mReader?.startInventoryTag()
        } else {
            mReader?.inventorySingleTag()
        }

        Thread {
            while (isScanning) {
                val tagInfo = mReader?.readTagFromBuffer()
                if (tagInfo != null) {
                    // Kirim ke handler untuk update UI
                    val msg = handler.obtainMessage()
                    msg.what = 3 // FLAG_UHFINFO
                    msg.obj = tagInfo
                    handler.sendMessage(msg)
                }
                Thread.sleep(10) // Delay kecil agar CPU tidak panas
            }
        }.start()
    }

    private fun stopInventory() {
        isScanning = false
        mReader?.stopInventory()

        btStop.isEnabled = false
        inventoryLoop.isEnabled = true
        btInventory.isEnabled = true
        btnSubmit.isEnabled = true
        btnCheckForeign.isEnabled = true
    }

    private fun addEPCToList(originalEPC: String?) {
        if (originalEPC.isNullOrEmpty()) return


        // Proses EPC string jika lebih dari 24 karakter
        var epc: String? = originalEPC
        if (originalEPC != null && originalEPC.length > 24) {
            // Ambil 24 karakter di tengah (buang 4 karakter awal dan 4 karakter akhir)
            val startIndex = 4
            val endIndex: Int = originalEPC.length - 4
            if (endIndex - startIndex >= 24) {
                epc = originalEPC.substring(startIndex, startIndex + 24)
            } else {
                // Jika setelah dipotong masih kurang dari 24, ambil semua yang tersisa
                epc = originalEPC.substring(startIndex, endIndex)
            }
        }

        // 1. Cari apakah EPC ini ada di dalam list yang kita dapat dari API
        val existingIndex = assetList.indexOfFirst { it.assRfidCode == epc }

        if (existingIndex != -1) {
            if (assetList[existingIndex].status == "3") {
                // Asset ini adalah foreign, biarkan statusnya tetap 3
                return
            }
            // JIKA ADA: Update status menjadi "Found" (1)
            if (assetList[existingIndex].status != "1" && assetList[existingIndex].assId != null) {
                assetList[existingIndex].status = "1"
                // Suara bip jika diperlukan
                // Utils.playSound(1)

                // Update RecyclerView secara spesifik di index tersebut agar tidak lag
                runOnUiThread {
                    rvAsset.adapter?.notifyItemChanged(existingIndex)
                }
            }
        } else {
            // JIKA TIDAK ADA: Cek apakah sudah ditambahkan sebagai Foreign sebelumnya
            val isAlreadyForeign = assetList.any { it.assRfidCode == epc && it.status == "3"}

            if (!isAlreadyForeign) {
                // Buat objek baru dengan status "Foreign" (3)
                val foreignAsset = AssetOpname(
                    assRfidCode = epc,
                    assCode = "Unknown",
                    assDesc = "Foreign Asset",
                    status = "3",
                    assId = null,
                    assOid = "",
                    assSn = "",
                    assBarcode = "",
                    assQty = "",
                    assLocId = "",
                    ptCode = "",
                    ptDesc1 = "",
                    ptDesc2 = "",
                    assropOid = "",
                    assropAssroOid = "",
                    assropAssrolOid = "",
                    assropDate = "",
                    assropQty = "",
                    assropForeignLoc = "",
                    assropIsInput = ""
                )

                assetList.add(foreignAsset)
                runOnUiThread {
                    rvAsset.adapter?.notifyItemInserted(assetList.size - 1)
                }
            }
        }
    }


    private fun observe() {
        viewModel.foreignAsset.observe(this) { responseAssets ->
            var updatedCount = 0

            responseAssets.forEach { newData ->
                // Cari asset di list lokal yang RFID-nya sama dengan hasil API
                val index = assetList.indexOfFirst { it.assRfidCode == newData.assRfidCode }

                if (index != -1) {
                    // Update data lama dengan data baru dari API
                    // Kita pertahankan status "3" atau sesuaikan dengan logika bisnis Anda
                    val updatedAsset = newData.copy(status = "3")
                    assetList[index] = updatedAsset
                    updatedCount++
                }
            }

            if (updatedCount > 0) {
                rvAsset.adapter?.notifyDataSetChanged()
                Toast.makeText(this, "$updatedCount Asset Updated dari Server", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Data dari server tidak cocok dengan list lokal", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Data Submitted Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { msg ->
            if (msg.isNotEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.assetList.observe(this) { list ->
            assetList.clear()
            assetList.addAll(list)
            rvAsset.adapter?.notifyDataSetChanged()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

            // Opsional: Disable tombol saat loading agar tidak terjadi double click
            btnSubmit.isEnabled = !isLoading
        }
    }

    private fun getForeignOpname(assetCode: String) {
        if (assetCode.isNotEmpty()) {
            viewModel.getForeignOpname(assetCode)
        } else {
            Toast.makeText(this, "Asset Code kosong", Toast.LENGTH_SHORT).show()
        }
    }

}