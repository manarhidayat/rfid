package com.example.uhf_bt.opname

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.opnameimport.OpnameViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint // WAJIB untuk Hilt
class OpnameDataActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddNew: Button
    private lateinit var btnSearch: Button
    private lateinit var etStartDate: EditText
    private lateinit var etToDate: EditText
    private lateinit var etSearch: EditText

    // Injeksi ViewModel menggunakan Activity KTX
    private val viewModel: OpnameViewModel by viewModels()
    private val calendar = Calendar.getInstance()

    private lateinit var progressBar: ProgressBar // Tambahkan ini

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opname_data)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Opname Data"
        }

        // Inisialisasi View
        recyclerView = findViewById(R.id.rv_opname_data)
        btnAddNew = findViewById(R.id.btn_add_new)
        btnSearch = findViewById(R.id.btn_search)
        etStartDate = findViewById(R.id.et_start_date)
        etToDate = findViewById(R.id.et_to_date)
        etSearch = findViewById(R.id.et_search)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        setupDatePicker(etStartDate)
        setupDatePicker(etToDate)

        // Observe Data dari ViewModel
        observeViewModel()

        // Tombol Search dengan Filter
        btnSearch.setOnClickListener {
            val code = etSearch.text.toString().trim()
            val start = etStartDate.text.toString().trim()
            val end = etToDate.text.toString().trim()

            viewModel.getOpnameData(
                code = if (code.isEmpty()) null else code,
                startDate = if (start.isEmpty()) null else start,
                endDate = if (end.isEmpty()) null else end
            )
        }

        btnAddNew.setOnClickListener {
            val intent = Intent(this, OpnameAddActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data setiap kali activity tampil kembali ke layar
        viewModel.getOpnameData()
    }

    private fun observeViewModel() {
        // Load Data Pertama Kali (Tanpa Filter)
        viewModel.getOpnameData()

        viewModel.opnameList.observe(this) { list ->
            val adapter = OpnameAdapter(list.toMutableList()) { opname ->
                val intent = Intent(this, OpnameLocationActivity::class.java)
                intent.putExtra("EXTRA_OPNAME", opname)
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        }

        viewModel.errorMessage.observe(this) { msg ->
            if (msg.isNotEmpty()) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupDatePicker(editText: EditText) {
        editText.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                editText.setText(date)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
}
