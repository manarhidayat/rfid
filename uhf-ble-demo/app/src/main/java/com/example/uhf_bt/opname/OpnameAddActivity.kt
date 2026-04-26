package com.example.uhf_bt.opname

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.uhf_bt.R
import com.example.uhf_bt.opnameimport.OpnameViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class OpnameAddActivity : AppCompatActivity() {

    private lateinit var etCode: EditText
    private lateinit var etDate: EditText

    private val viewModel: OpnameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opname_add)

        etCode = findViewById<EditText>(R.id.et_opname_code) // Pastikan ID ada di layout
        etDate = findViewById<EditText>(R.id.et_opname_date)
        val btnSave = findViewById<Button>(R.id.btn_save)

        // Supaya user tidak bisa mengubah kode manual (karena otomatis dari sistem)
        etCode.isEnabled = false

        // 2. Observasi hasilnya
        setupObservers()

        // Setup Date Picker (Sudah benar)
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                // Gunakan format yyyy-MM-dd agar konsisten dengan API
                val dateString = String.format("%04d-%02d-%02d", year, month + 1, day)
                etDate.setText(dateString)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }



        btnSave.setOnClickListener {
            val code = etCode.text.toString().trim()
            val date = etDate.text.toString().trim()

            if (code.isNotEmpty() && date.isNotEmpty()) {
                viewModel.addOpname(code, date)
            } else {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setupObservers() {
        // 1. Jalankan fungsi ambil kode
        viewModel.fetchNextCode()

        // Observe Result
        viewModel.isSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Data Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                finish() // Tutup activity dan kembali
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }

        // Jika berhasil mendapatkan kode
        viewModel.nextCode.observe(this) { code ->
            etCode.setText(code)
        }

        // Jika terjadi error
        viewModel.error.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}