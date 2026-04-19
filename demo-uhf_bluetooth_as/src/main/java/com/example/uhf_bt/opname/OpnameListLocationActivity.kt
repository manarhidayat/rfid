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

class OpnameListLocationActivity : AppCompatActivity() {

    private lateinit var tvCode: TextView
    private lateinit var tvLocation: TextView
    private lateinit var rvAsset: RecyclerView
    private lateinit var btnSubmit: Button
    private var assetList = mutableListOf<AssetOpname>()

    private val viewModel: OpnameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location_list_location)

        // Inisialisasi View
        tvCode = findViewById(R.id.tv_list_opname_code)
        tvLocation = findViewById(R.id.tv_list_location_name)
        rvAsset = findViewById(R.id.rv_asset_list)
        btnSubmit = findViewById(R.id.btn_submit_opname)

        // 1. Tangkap Data dari Intent (Bisa dari OpnameData atau OpnameLocation)
        val opnameData = intent.getSerializableExtra("EXTRA_OPNAME") as? Opname
        val locationData = intent.getSerializableExtra("EXTRA_LOCATION") as? OpnameLocation

        tvCode.text = "Stock Opname: ${opnameData?.code ?: "-"}"
        tvLocation.text = "Location: ${locationData?.location ?: "-"}"

        // 2. Setup Data Dummy
//        prepareDummyData()

        // 3. Setup RecyclerView
        rvAsset.layoutManager = LinearLayoutManager(this)
        rvAsset.adapter = OpnameListLocationAdapter(assetList)

        // 4. Submit Button
        btnSubmit.setOnClickListener {
            Toast.makeText(this, "Data Submitted Successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Gunakan opnameData?.no (asumsi property .no ada di model Opname)
        opnameData?.let { viewModel.getOpnameListLocation(it.id?.toInt() ?: 0) }

        viewModel.assetList.observe(this) { list ->
            assetList.clear()
            assetList.addAll(list)
            rvAsset.adapter?.notifyDataSetChanged()
        }

        findViewById<Button>(R.id.btn_search).setOnClickListener {
            val query = findViewById<EditText>(R.id.et_search_asset).text.toString().trim()
            opnameData?.let { data -> viewModel.getOpnameListLocation(opnameData.id?.toInt() ?: 0, query) }
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

        // Update listener button submit
        btnSubmit.setOnClickListener {
            if (assetList.isNotEmpty()) {
                viewModel.submitOpnameListLocation(assetList)
            } else {
                Toast.makeText(this, "No data to submit", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.foreignAsset.observe(this) { asset ->
            // Tambahkan ke list dan beritahu adapter
            assetList.add(asset)
            rvAsset.adapter?.notifyItemInserted(assetList.size - 1)
            rvAsset.scrollToPosition(assetList.size - 1)

            Toast.makeText(this, "Foreign Asset Added: ${asset.assetName}", Toast.LENGTH_SHORT).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getForeignOpname(assetCode: String) {
        if (assetCode.isNotEmpty()) {
            viewModel.getForeignOpname(assetCode)
        } else {
            Toast.makeText(this, "Asset Code kosong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun prepareDummyData() {
        assetList.add(AssetOpname(1, "AST-2023-001", "Macbook Pro M2", "Found"))
        assetList.add(AssetOpname(2, "AST-2023-002", "Monitor Dell 24 Inch", "Not Found"))
        assetList.add(AssetOpname(3, "AST-2023-099", "Keyboard Mechanical (Unknown)", "Foreign"))
        assetList.add(AssetOpname(4, "AST-2023-005", "Office Chair", "Found"))
    }
}