package com.example.uhf_bt.opname

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
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
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.opnameimport.OpnameViewModel
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import dagger.hilt.android.AndroidEntryPoint
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class OpnameReportActivity : AppCompatActivity() {

    private lateinit var etStartDate: EditText
    private lateinit var etToDate: EditText
    private lateinit var rvReport: RecyclerView
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var startDateSelected: Long = 0
    private var toDateSelected: Long = Long.MAX_VALUE

    // Simpan data list secara global agar bisa diakses fungsi export
    private var reportList: List<Opname> = listOf()

    private val viewModel: OpnameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opname_report)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etStartDate = findViewById(R.id.et_start_date)
        etToDate = findViewById(R.id.et_to_date)
        rvReport = findViewById(R.id.rv_report)

        setupDatePickers()
        setupRecyclerView()

        findViewById<Button>(R.id.btn_download_pdf_all).setOnClickListener {
            if (reportList.isNotEmpty()) {
                exportToPDF(reportList)
            } else {
                Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btn_download_excel_all).setOnClickListener {
            if (reportList.isNotEmpty()) {
                exportToExcel(reportList)
            } else {
                Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btn_search_report).setOnClickListener {
            val code = findViewById<EditText>(R.id.et_search_report).text.toString()
            val start = etStartDate.text.toString()
            val end = etToDate.text.toString()

            viewModel.getOpnameReport(
                if(start.isEmpty()) null else start,
                if(end.isEmpty()) null else end,
                if(code.isEmpty()) null else code
            )
        }

        viewModel.opnameList.observe(this) { list ->
            reportList = list
            setupRecyclerView() // Refresh adapter dengan data baru
        }
    }

    private fun setupRecyclerView() {
        reportList = listOf(
            Opname(
                "1", "OPN-20231001-001",
                assroAddBy = TODO(),
                assroAddDate = TODO(),
                assroUpdBy = TODO(),
                assroUpdDate = TODO(),
                assroCode = TODO(),
                assroDesc = TODO(),
                assroStartDate = TODO(),
                assroEndDate = TODO(),
                assroStatus = TODO(),
                assroDt = TODO()
            )
        )

        rvReport.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter dengan listener untuk tiap item
        rvReport.adapter = OpnameReportAdapter(
            reportList,
            { selectedItem ->
                // Klik PDF pada item: Bungkus item tunggal ke dalam list
                exportToPDF(listOf(selectedItem))
            },
            { selectedItem ->
                // Klik Excel pada item: Bungkus item tunggal ke dalam list
                exportToExcel(listOf(selectedItem))
            }
        )
    }

    private fun exportToPDF(data: List<Opname>) {
        try {
            val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
            val file = File(path, "Opname_Report_${System.currentTimeMillis()}.pdf")
            val writer = PdfWriter(FileOutputStream(file))
            val pdf = PdfDocument(writer)
            val document = Document(pdf)

            document.add(Paragraph("Stock Opname Report").setBold().setFontSize(18f))
            document.add(Paragraph("Export Date: ${dateFormat.format(Date())}"))
            document.add(Paragraph("\n"))

            val table = Table(floatArrayOf(1f, 3f, 2f)) // Kolom: No, Code, Date
            table.addCell("No")
            table.addCell("Stock Opname Code")
            table.addCell("Date")

            data.forEachIndexed { index, opname ->
                table.addCell((index + 1).toString())
                table.addCell(opname.assroCode)
                table.addCell(opname.assroStartDate)
            }

            document.add(table)
            document.close()

            Toast.makeText(this, "PDF Saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to export PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportToExcel(data: List<Opname>) {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Opname Report")

            // Header
            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("No")
            headerRow.createCell(1).setCellValue("Stock Opname Code")
            headerRow.createCell(2).setCellValue("Date")

            // Body
            data.forEachIndexed { index, opname ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue((index + 1).toDouble())
                row.createCell(1).setCellValue(opname.assroCode)
                row.createCell(2).setCellValue(opname.assroStartDate)
            }

            val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
            val file = File(path, "Opname_Report_${System.currentTimeMillis()}.xlsx")
            val out = FileOutputStream(file)
            workbook.write(out)
            out.close()
            workbook.close()

            Toast.makeText(this, "Excel Saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to export Excel: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDatePickers() {
        etStartDate.setOnClickListener {
            val dpd = DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
                val selected = calendar.timeInMillis
                if (selected > toDateSelected) {
                    Toast.makeText(this, "Start Date cannot be greater than To Date", Toast.LENGTH_SHORT).show()
                } else {
                    startDateSelected = selected
                    etStartDate.setText(dateFormat.format(calendar.time))
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            dpd.show()
        }

        etToDate.setOnClickListener {
            val dpd = DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
                val selected = calendar.timeInMillis
                if (selected < startDateSelected) {
                    Toast.makeText(this, "To Date cannot be less than Start Date", Toast.LENGTH_SHORT).show()
                } else {
                    toDateSelected = selected
                    etToDate.setText(dateFormat.format(calendar.time))
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            dpd.show()
        }
    }
}