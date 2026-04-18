package com.example.uhf_bt.opname

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.model.OpnameLocation

class OpnameLocationActivity : AppCompatActivity() {

    private lateinit var tvOpnameCode: TextView
    private lateinit var tvOpnameDate: TextView
    private lateinit var etSearch: EditText
    private lateinit var rvLocation: RecyclerView
    private lateinit var adapter: OpnameLocationAdapter
    private var locationList = mutableListOf<OpnameLocation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opname_location)

        // Setup Window Insets (Padding System Bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Inisialisasi View
        tvOpnameCode = findViewById(R.id.tv_opname_code)
        tvOpnameDate = findViewById(R.id.tv_opname_date)
        etSearch = findViewById(R.id.et_search_location)
        rvLocation = findViewById(R.id.rv_opname_location)

        // 2. Tangkap data dari Intent (OpnameDataActivity)
        val opnameData = intent.getSerializableExtra("EXTRA_OPNAME") as? Opname
        opnameData?.let {
            tvOpnameCode.text = "Code: ${it.code}" // Asumsi Opname punya properti code
            tvOpnameDate.text = "Date: ${it.date}" // Asumsi Opname punya properti date
        }

        // 3. Setup Data Dummy
        prepareDummyData()

        // 4. Setup RecyclerView
        adapter = OpnameLocationAdapter(
            locationList,
            { position ->
                showStatusDialog(position)
            },
            { selectedLocation ->
                // Navigasi ke OpnameListLocationActivity
                val intent = Intent(this, OpnameListLocationActivity::class.java)

                // Kirim data Opname induk (code & date)
                intent.putExtra("EXTRA_OPNAME", opnameData)

                // Kirim data Lokasi terpilih (name)
                intent.putExtra("EXTRA_LOCATION", selectedLocation)

                startActivity(intent)
            }
        )
        rvLocation.layoutManager = LinearLayoutManager(this)
        rvLocation.adapter = adapter
    }

    private fun prepareDummyData() {
        locationList.add(OpnameLocation(1, "Gudang A - Rak 01", "Open"))
        locationList.add(OpnameLocation(2, "Gudang A - Rak 02", "Done"))
        locationList.add(OpnameLocation(3, "Gudang B - Sektor Utama", "Open"))
        locationList.add(OpnameLocation(4, "Gudang C", "Open"))
    }

    private fun showStatusDialog(position: Int) {
        val options = arrayOf("Open", "Done")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Status")
        builder.setItems(options) { _, dispensed ->
            val selectedStatus = options[dispensed]
            locationList[position].status = selectedStatus
            adapter.notifyItemChanged(position)
        }
        builder.show()
    }
}