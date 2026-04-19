package com.example.uhf_bt.opname

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.model.OpnameLocation
import com.example.uhf_bt.opnameimport.OpnameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OpnameLocationActivity : AppCompatActivity() {

    private lateinit var tvOpnameCode: TextView
    private lateinit var tvOpnameDate: TextView
    private lateinit var etSearch: EditText
    private lateinit var rvLocation: RecyclerView
    private lateinit var btnSearch: Button
    private lateinit var adapter: OpnameLocationAdapter
    private var locationList = mutableListOf<OpnameLocation>()

    private val viewModel: OpnameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opname_location)

        // 1. Inisialisasi View
        tvOpnameCode = findViewById(R.id.tv_opname_code)
        tvOpnameDate = findViewById(R.id.tv_opname_date)
        etSearch = findViewById(R.id.et_search_location)
        rvLocation = findViewById(R.id.rv_opname_location)
        btnSearch = findViewById(R.id.btn_search)

        prepareDummyData()

        val opnameData = intent.getSerializableExtra("EXTRA_OPNAME") as? Opname
        opnameData?.let {
            tvOpnameCode.text = "Code: ${it.code}"
            tvOpnameDate.text = "Date: ${it.date}"

            // Panggil API Pertama Kali
//            viewModel.getOpnameLocation(it.code.toString())
        }

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            opnameData?.let { data -> viewModel.getOpnameLocation(data.code.toString(), query) }
        }

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

    private fun observe() {
        // Observe data dari API
        viewModel.locationList.observe(this) { list ->
            locationList.clear()
            locationList.addAll(list)
            adapter.notifyDataSetChanged()
        }
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
            val locationItem = locationList[position]

            // Ambil data opname dari intent untuk mendapatkan kodenya
            val opnameData = intent.getSerializableExtra("EXTRA_OPNAME") as? Opname

            opnameData?.let {
                // Panggil API melalui ViewModel
                // Parameter: Kode Opname, ID/No Lokasi, dan Status Baru
                viewModel.updateOpnameLocation(
                    it.code.toString(),
                    locationItem.no,
                    selectedStatus
                )
            }

            // Opsional: Update UI lokal sementara (akan di-refresh otomatis oleh observer viewModel.locationList)
            locationList[position].status = selectedStatus
            adapter.notifyItemChanged(position)
        }
        builder.show()
    }
}