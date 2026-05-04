package com.example.uhf_bt.opname

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.Opname

class OpnameReportAdapter(
    private val list: List<Opname>,
    private val onPdfClick: (Opname) -> Unit, // Callback PDF
    private val onExcelClick: (Opname) -> Unit // Callback Excel
) : RecyclerView.Adapter<OpnameReportAdapter.ViewHolder>() {

    private var isSelectionMode = false
    private val selectedItems = mutableSetOf<Opname>()

    fun setSelectionMode(enabled: Boolean) {
        isSelectionMode = enabled
        if (!enabled) selectedItems.clear() // Reset pilihan jika mode dimatikan
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItems() = selectedItems.toList()

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvNo: TextView = v.findViewById(R.id.tv_no)
        val tvCode: TextView = v.findViewById(R.id.tv_code)
        val tvDate: TextView = v.findViewById(R.id.tv_date)
        val btnPdf: ImageButton = v.findViewById(R.id.btn_pdf)
        val btnExcel: ImageButton = v.findViewById(R.id.btn_excel)
        val checkBox: CheckBox = v.findViewById(R.id.cb_item) // Hubungkan ke XML
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_opname_report, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvNo.text = (position + 1).toString()
        holder.tvCode.text = item.assroCode
        holder.tvDate.text = item.assroStartDate

        // Kontrol Visibilitas: Jika mode seleksi, munculkan checkbox, sembunyikan tombol aksi
        if (isSelectionMode) {
            holder.checkBox.visibility = View.VISIBLE
            holder.btnPdf.visibility = View.GONE
            holder.btnExcel.visibility = View.GONE
        } else {
            holder.checkBox.visibility = View.GONE
            holder.btnPdf.visibility = View.VISIBLE
            holder.btnExcel.visibility = View.VISIBLE
        }

        // Penting: Hapus listener lama sebelum mengatur status isChecked untuk menghindari bug recycling
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = selectedItems.contains(item)

        // Set listener baru untuk menyimpan pilihan user
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(item)
            } else {
                selectedItems.remove(item)
            }
        }

        holder.btnPdf.setOnClickListener { onPdfClick(item) }
        holder.btnExcel.setOnClickListener { onExcelClick(item) }
    }

    override fun getItemCount() = list.size
}