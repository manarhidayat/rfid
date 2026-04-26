package com.example.uhf_bt.opname

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvNo: TextView = v.findViewById(R.id.tv_no)
        val tvCode: TextView = v.findViewById(R.id.tv_code)
        val tvDate: TextView = v.findViewById(R.id.tv_date)
        val btnPdf: ImageButton = v.findViewById(R.id.btn_pdf)
        val btnExcel: ImageButton = v.findViewById(R.id.btn_excel)
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

        holder.btnPdf.setOnClickListener { onPdfClick(item) }
        holder.btnExcel.setOnClickListener { onExcelClick(item) }
    }

    override fun getItemCount() = list.size
}