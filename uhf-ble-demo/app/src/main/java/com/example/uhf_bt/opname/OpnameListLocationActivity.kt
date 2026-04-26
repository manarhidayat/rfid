package com.example.uhf_bt.opname

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.AssetOpname
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.model.OpnameLocation
import com.example.uhf_bt.opnameimport.OpnameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OpnameListLocationActivity : AppCompatActivity() {

    private lateinit var tvCode: TextView
    private lateinit var tvLocation: TextView
    private lateinit var rvAsset: RecyclerView
    private lateinit var btnSubmit: Button
    private var assetList = mutableListOf<AssetOpname>()

    private val viewModel: OpnameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list_location)

        // Inisialisasi View
        tvCode = findViewById(R.id.tv_list_opname_code)
        tvLocation = findViewById(R.id.tv_list_location_name)
        rvAsset = findViewById(R.id.rv_asset_list)
        btnSubmit = findViewById(R.id.btn_submit_opname)

        // 1. Tangkap Data dari Intent (Bisa dari OpnameData atau OpnameLocation)
        val opnameData = intent.getSerializableExtra("EXTRA_OPNAME") as? Opname
        val locationData = intent.getSerializableExtra("EXTRA_LOCATION") as? OpnameLocation

        tvCode.text = "Stock Opname: ${opnameData?.assroCode ?: "-"}"
        tvLocation.text = "Location: ${locationData?.assrolLocId ?: "-"}"

        // 3. Setup RecyclerView
        rvAsset.layoutManager = LinearLayoutManager(this)
        rvAsset.adapter = OpnameListLocationAdapter(assetList)

        // 4. Submit Button
        btnSubmit.setOnClickListener {
            Toast.makeText(this, "Data Submitted Successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Gunakan opnameData?.no (asumsi property .no ada di model Opname)
        locationData?.let { viewModel.getOpnameListLocation(it.assrolLocId ?: "") }

        findViewById<Button>(R.id.btn_search).setOnClickListener {
            val query = findViewById<EditText>(R.id.et_search_asset).text.toString().trim()
            opnameData?.let { data -> viewModel.getOpnameListLocation(locationData?.assrolLocId ?: "", query) }
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

        observe()
    }

    private fun observe() {
        viewModel.foreignAsset.observe(this) { asset ->
            // Tambahkan ke list dan beritahu adapter
            assetList.add(asset)
            rvAsset.adapter?.notifyItemInserted(assetList.size - 1)
            rvAsset.scrollToPosition(assetList.size - 1)

            Toast.makeText(this, "Foreign Asset Added: ${asset.assCode}", Toast.LENGTH_SHORT).show()
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
    }

    private fun getForeignOpname(assetCode: String) {
        if (assetCode.isNotEmpty()) {
            viewModel.getForeignOpname(assetCode)
        } else {
            Toast.makeText(this, "Asset Code kosong", Toast.LENGTH_SHORT).show()
        }
    }

}