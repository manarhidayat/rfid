package com.example.uhf_bt.opname

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.AssetOpname
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.model.OpnameLocation

class OpnameListLocationActivity : AppCompatActivity() {

    private lateinit var tvCode: TextView
    private lateinit var tvLocation: TextView
    private lateinit var rvAsset: RecyclerView
    private lateinit var btnSubmit: Button
    private var assetList = mutableListOf<AssetOpname>()

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
        prepareDummyData()

        // 3. Setup RecyclerView
        rvAsset.layoutManager = LinearLayoutManager(this)
        rvAsset.adapter = OpnameListLocationAdapter(assetList)

        // 4. Submit Button
        btnSubmit.setOnClickListener {
            Toast.makeText(this, "Data Submitted Successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun prepareDummyData() {
        assetList.add(AssetOpname(1, "AST-2023-001", "Macbook Pro M2", "Found"))
        assetList.add(AssetOpname(2, "AST-2023-002", "Monitor Dell 24 Inch", "Not Found"))
        assetList.add(AssetOpname(3, "AST-2023-099", "Keyboard Mechanical (Unknown)", "Foreign"))
        assetList.add(AssetOpname(4, "AST-2023-005", "Office Chair", "Found"))
    }
}