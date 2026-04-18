package com.example.uhf_bt.opname

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.uhf_bt.R
import java.util.*

class OpnameAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opname_add)

        val etDate = findViewById<EditText>(R.id.et_opname_date)
        val btnSave = findViewById<Button>(R.id.btn_save)

        // Setup Date Picker
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    val dateString = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etDate.setText(dateString)
                }, year, month, day)

            datePickerDialog.show()
        }

        btnSave.setOnClickListener {
            // Logika simpan data di sini
            finish() // Kembali ke halaman sebelumnya
        }
    }
}