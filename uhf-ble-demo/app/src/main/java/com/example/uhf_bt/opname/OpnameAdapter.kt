package com.example.uhf_bt.opname

import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.Opname

class OpnameAdapter(
    private val list: MutableList<Opname>,
    private val onItemClick: (Opname) -> Unit // Tambahkan callback klik di sini
) : RecyclerView.Adapter<OpnameAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNo: TextView = view.findViewById(R.id.tv_no)
        val tvCode: TextView = view.findViewById(R.id.tv_code)
        val tvDesc: TextView = view.findViewById(R.id.tv_desc)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_opname, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvNo.text = "${position + 1}"
        holder.tvCode.text = item.assroOid
        holder.tvDate.text = item.assroStartDate
        holder.tvDesc.text = item.assroDesc

        // Set listener klik pada item
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = list.size
}