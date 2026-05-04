package com.example.uhf_bt.opname

import OpnameReport
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.Opname
import com.example.uhf_bt.model.OpnameReportPdf
import com.example.uhf_bt.opnameimport.OpnameViewModel
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import dagger.hilt.android.AndroidEntryPoint
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class OpnameReportActivity : AppCompatActivity() {

    private lateinit var etStartDate: EditText
    private lateinit var etToDate: EditText
    private lateinit var etSearch: EditText
    private lateinit var rvReport: RecyclerView
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var startDateSelected: Long = 0
    private var toDateSelected: Long = Long.MAX_VALUE

    // Simpan data list secara global agar bisa diakses fungsi export
    private var reportList: List<Opname> = listOf()

    private val viewModel: OpnameViewModel by viewModels()

    private lateinit var progressBar: ProgressBar // Tambahkan ini
    // Tambahkan variabel global di dalam class
    private var isSelectionMode = false
    private var exportType = "" // "PDF" atau "EXCEL"
    private lateinit var layoutSelectionMode: View
    private lateinit var btnConfirmExport: Button
    private lateinit var btnCancelExport: Button

    private val PERMISSION_REQUEST_CODE = 100
    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.READ_MEDIA_IMAGES, // Ganti READ_EXTERNAL_STORAGE untuk foto
            android.Manifest.permission.READ_MEDIA_VIDEO,  // Untuk video
            android.Manifest.permission.CAMERA             // Izin Kamera
        )
    } else {
        arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opname_report)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Opname Report"
        }

        etStartDate = findViewById(R.id.et_start_date)
        etToDate = findViewById(R.id.et_to_date)
        etSearch = findViewById(R.id.et_search_report)
        rvReport = findViewById(R.id.rv_report)
        progressBar = findViewById(R.id.progressBar)
        layoutSelectionMode = findViewById(R.id.layout_selection_mode)
        btnConfirmExport = findViewById(R.id.btn_confirm_export)
        btnCancelExport = findViewById(R.id.btn_cancel_export)

        setupDatePickers()
        setupRecyclerView()

        findViewById<Button>(R.id.btn_download_pdf_all).setOnClickListener {
            enterSelectionMode("PDF")
        }

        findViewById<Button>(R.id.btn_download_excel_all).setOnClickListener {
            enterSelectionMode("EXCEL")
        }

        findViewById<Button>(R.id.btn_search_report).setOnClickListener {
            getData()
        }

        btnCancelExport.setOnClickListener {
            exitSelectionMode()
        }

        btnConfirmExport.setOnClickListener {
            val adapter = rvReport.adapter as OpnameReportAdapter
            val selectedItems = adapter.getSelectedItems()

            if (selectedItems.isNotEmpty()) {
                val commaSeparatedOids = selectedItems.mapNotNull { it.assroOid }.joinToString(",")

                progressBar.visibility = View.VISIBLE
                if (exportType == "PDF") {
                    viewModel.getOpnameReportPdf(commaSeparatedOids)
                } else {
                    viewModel.getOpnameReport(commaSeparatedOids)
                }
            } else {
                Toast.makeText(this, "Please select at least one item", Toast.LENGTH_SHORT).show()
            }
        }

        getData()
        observe()
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions(): Boolean {
        val listPermissionsNeeded = mutableListOf<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission)
            }
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
            return false
        }
        return true
    }

    // Override callback hasil request
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Permissions Denied. You might not see notifications.", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    private fun enterSelectionMode(type: String) {
        isSelectionMode = true
        exportType = type
        layoutSelectionMode.visibility = View.VISIBLE
        (rvReport.adapter as OpnameReportAdapter).setSelectionMode(true)
    }

    private fun exitSelectionMode() {
        isSelectionMode = false
        exportType = ""
        layoutSelectionMode.visibility = View.GONE
        (rvReport.adapter as OpnameReportAdapter).let {
            it.setSelectionMode(false)
            it.clearSelection()
        }
    }

    fun getData() {
        val code = etSearch.text.toString().trim()
        val start = etStartDate.text.toString().trim()
        val end = etToDate.text.toString().trim()

        viewModel.getOpnameData(
            code = if (code.isEmpty()) null else code,
            startDate = if (start.isEmpty()) null else start,
            endDate = if (end.isEmpty()) null else end
        )
    }

    fun observe() {
        viewModel.opnameList.observe(this) { list ->
            reportList = list
            setupRecyclerView() // Refresh adapter dengan data baru
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        }

        viewModel.opnameReportPdf.observe(this) { listPdf ->
            if (isSelectionMode && exportType == "PDF") {
                if (listPdf.isNotEmpty()) {
                    exportToPdf(listPdf)
                    exitSelectionMode()
                } else {
                    Toast.makeText(this, "Data PDF tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.opnameReport.observe(this) { listExcel ->
            if (isSelectionMode && exportType == "EXCEL") {
                if (listExcel.isNotEmpty()) {
                    exportToExcel(listExcel)
                    exitSelectionMode()
                } else {
                    Toast.makeText(this, "Data Excel tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {

        rvReport.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter dengan listener untuk tiap item
        rvReport.adapter = OpnameReportAdapter(
            reportList,
            { selectedItem ->
                // Klik PDF pada item: Bungkus item tunggal ke dalam list
                isSelectionMode = true
                exportType = "PDF"
                viewModel.getOpnameReportPdf(selectedItem.assroOid)
            },
            { selectedItem ->
                // Klik Excel pada item: Bungkus item tunggal ke dalam list
                isSelectionMode = true
                exportType = "EXCEL"
                viewModel.getOpnameReport(selectedItem.assroOid)
            }
        )
    }

    private fun exportToPdf(data: List<OpnameReportPdf>) {
        try {
            val fileName = "Opname_Report_${System.currentTimeMillis()}.pdf"
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            val writer = PdfWriter(FileOutputStream(file))
            val pdf = PdfDocument(writer)
            val document = Document(pdf, PageSize.A4.rotate()) // Landscape agar tabel lega
            document.setMargins(20f, 20f, 20f, 20f)

            // 1. Title & Tanggal Cetak
            document.add(Paragraph("Stock Opname Report").setBold().setFontSize(18f))
            val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            document.add(Paragraph("Tanggal Cetak: $currentDate").setFontSize(10f))
            document.add(Paragraph("\n"))

            data.forEach { opname ->
                // 2. Header Opname
                document.add(Paragraph("Stock Opname Code: ${opname.assroCode ?: "-"}").setBold().setFontSize(12f))

                opname.locations?.forEach { location ->
                    // 3. Header Location
                    document.add(Paragraph("Location: ${location.locDesc ?: "-"}").setItalic().setMarginLeft(10f))

                    // Pisahkan data Normal dan Foreign (status == "3")
                    val normalItems = location.items?.filter { it.assropStatus != "3" } ?: listOf()
                    val foreignItems = location.items?.filter { it.assropStatus == "3" } ?: listOf()

                    // --- TABEL DATA NORMAL ---
                    if (normalItems.isNotEmpty()) {
                        val tableNormal = Table(floatArrayOf(1f, 2f, 3f, 4f, 2f)).useAllAvailableWidth()
                        val headersNormal = arrayOf("No", "SO Date", "Asset ID", "Asset Name", "Category")

                        headersNormal.forEach {
                            tableNormal.addHeaderCell(Cell().add(Paragraph(it).setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY))
                        }

                        normalItems.forEachIndexed { index, item ->
                            tableNormal.addCell(Paragraph((index + 1).toString()))
                            tableNormal.addCell(Paragraph(item.assropDate ?: "-"))
                            tableNormal.addCell(Paragraph(item.assCode ?: "-"))
                            tableNormal.addCell(Paragraph(item.assDesc ?: "-"))
                            tableNormal.addCell(Paragraph(item.category ?: "-"))
                        }
                        document.add(tableNormal.setMarginLeft(10f).setMarginBottom(10f))
                    }

                    // --- TABEL DATA FOREIGN ---
                    if (foreignItems.isNotEmpty()) {
                        document.add(Paragraph("List Foreign Asset:").setBold().setFontSize(10f).setMarginLeft(10f).setFontColor(ColorConstants.RED))

                        val tableForeign = Table(floatArrayOf(1f, 2f, 3f, 4f, 2f, 3f)).useAllAvailableWidth()
                        val headersForeign = arrayOf("No", "SO Date", "Asset ID", "Asset Name", "Category", "Prev Location")

                        headersForeign.forEach {
                            tableForeign.addHeaderCell(Cell().add(Paragraph(it).setBold()).setBackgroundColor(ColorConstants.ORANGE))
                        }

                        foreignItems.forEachIndexed { index, item ->
                            tableForeign.addCell(Paragraph((index + 1).toString()))
                            tableForeign.addCell(Paragraph(item.assropDate ?: "-"))
                            tableForeign.addCell(Paragraph(item.assCode ?: "-"))
                            tableForeign.addCell(Paragraph(item.assDesc ?: "-"))
                            tableForeign.addCell(Paragraph(item.category ?: "-"))
                            tableForeign.addCell(Paragraph(item.previousLocation ?: "-"))
                        }
                        document.add(tableForeign.setMarginLeft(10f).setMarginBottom(10f))
                    }

                    if (normalItems.isEmpty() && foreignItems.isEmpty()) {
                        document.add(Paragraph("No items found in this location").setFontSize(9f).setItalic().setMarginLeft(20f))
                    }
                }
                document.add(Paragraph("\n---\n")) // Separator antar opname
            }

            document.close()
            Toast.makeText(this, "PDF Berhasil disimpan", Toast.LENGTH_SHORT).show()
            showNotification(file)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal export PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportToExcel(data: List<OpnameReport>) {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Opname Report")
            var currentRow = 0

            // 1. Definisikan Styles
            val titleStyle = workbook.createCellStyle().apply {
                val font = workbook.createFont().apply {
                    bold = true
                    fontHeightInPoints = 16.toShort()
                }
                setFont(font)
                alignment = HorizontalAlignment.LEFT
            }

            val dateStyle = workbook.createCellStyle().apply {
                val font = workbook.createFont().apply {
                    italic = true
                }
                setFont(font)
            }

            val headerStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
                borderTop = BorderStyle.THIN
                borderBottom = BorderStyle.THIN
                borderLeft = BorderStyle.THIN
                borderRight = BorderStyle.THIN
                alignment = HorizontalAlignment.CENTER
                val font = workbook.createFont().apply { bold = true }
                setFont(font)
            }

            val dataStyle = workbook.createCellStyle().apply {
                borderTop = BorderStyle.THIN
                borderBottom = BorderStyle.THIN
                borderLeft = BorderStyle.THIN
                borderRight = BorderStyle.THIN
            }

            // 2. Tambahkan Title (Baris 0)
            val titleRow = sheet.createRow(currentRow++)
            titleRow.createCell(0).apply {
                setCellValue("Stock Opname Report")
                setCellStyle(titleStyle)
            }
            // Merge cell dari kolom 0 sampai 7 (sesuai jumlah header)
            sheet.addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7))

            // 3. Tambahkan Tanggal Cetak (Baris 1)
            val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            val dateRow = sheet.createRow(currentRow++)
            dateRow.createCell(0).apply {
                setCellValue("Tanggal Cetak: $currentDate")
                setCellStyle(dateStyle)
            }
            sheet.addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 7))

            // 4. Baris Kosong (Baris 2)
            currentRow++

            // 1. Header Row
            val headerRow = sheet.createRow(currentRow++)
            val headers = arrayOf(
                "Stock Opname Code", "Stock Opname Date", "Location",
                "Asset Code", "Asset Name", "Category", "Status", "Previous Location"
            )

            headers.forEachIndexed { i, title ->
                headerRow.createCell(i).apply {
                    setCellValue(title)
                    setCellStyle(headerStyle)
                }
            }

            // 2. Data Rows
            data.forEach { report ->
                val row = sheet.createRow(currentRow++)

                row.createCell(0).apply { setCellValue(report.opname?.assroCode); setCellStyle(dataStyle) }
                row.createCell(1).apply { setCellValue(report.item?.assropDate); setCellStyle(dataStyle) }
                row.createCell(2).apply { setCellValue(report.location?.locDesc); setCellStyle(dataStyle) }
                row.createCell(3).apply { setCellValue(report.item?.assCode); setCellStyle(dataStyle) }
                row.createCell(4).apply { setCellValue(report.item?.assDesc); setCellStyle(dataStyle) }
                row.createCell(5).apply { setCellValue(report.item?.category); setCellStyle(dataStyle) }

                val statusText = when(report.item?.assropStatus) {
                    "1" -> "Found"
                    "2" -> "Not Found"
                    "3" -> "Foreign"
                    else -> report.item?.assropStatus
                }
                row.createCell(6).apply { setCellValue(statusText); setCellStyle(dataStyle) }
                row.createCell(7).apply { setCellValue(report.item?.previousLocation); setCellStyle(dataStyle) }
            }

            sheet.setColumnWidth(0, 20 * 256) // Stock Opname Code
            sheet.setColumnWidth(1, 20 * 256) // Stock Opname Date
            sheet.setColumnWidth(2, 25 * 256) // Location
            sheet.setColumnWidth(3, 35 * 256) // Asset Code (Biasanya panjang)
            sheet.setColumnWidth(4, 40 * 256) // Asset Name (Biasanya panjang)
            sheet.setColumnWidth(5, 20 * 256) // Category
            sheet.setColumnWidth(6, 15 * 256) // Status
            sheet.setColumnWidth(7, 25 * 256) // Previous Location


            // Save File
            val fileName = "Opname_Report_${System.currentTimeMillis()}.xlsx"
            val filePath = File(getExternalFilesDir(null), fileName)
            val out = FileOutputStream(filePath)
            workbook.write(out)
            out.close()
            workbook.close()

            Toast.makeText(this, "Excel Saved", Toast.LENGTH_SHORT).show()
            showNotification(filePath)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to export Excel: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun showNotification(file: File) {
        val channelId = "export_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat Notification Channel untuk Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Export Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Mendapatkan URI file menggunakan FileProvider
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "com.example.uhf_bt.fileprovider",
            file
        )

        // Tentukan MIME Type berdasarkan ekstensi file
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

        // Intent untuk membuka file
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo) // Ganti dengan icon download Anda
            .setContentTitle("Export Berhasil")
            .setContentText("Klik untuk membuka file: ${file.name}")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(1001, builder.build())
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