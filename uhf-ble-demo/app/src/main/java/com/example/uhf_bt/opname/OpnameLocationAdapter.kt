package com.example.uhf_bt.opname

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.OpnameLocation

class OpnameLocationAdapter(
    private val list: List<OpnameLocation>,
    private val onStatusClick: (Int) -> Unit,
    private val onActionClick: (OpnameLocation) -> Unit // Tambahkan callback action
) : RecyclerView.Adapter<OpnameLocationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNo: TextView = view.findViewById(R.id.tv_no)
        val tvLocation: TextView = view.findViewById(R.id.tv_location_name)
        val btnStatus: Button = view.findViewById(R.id.btn_status)
        val btnAction: Button = view.findViewById(R.id.btn_action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_opname_location, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvNo.text = "${position + 1}"
        holder.tvLocation.text = item.locDesc
        holder.btnStatus.text = item.assrolStatus

        if(item.assrolStatus == "-") {
            holder.btnAction.visibility = View.INVISIBLE
        }else {
            if(item.assrolStatus == "O") {
                holder.btnStatus.text = "Open"
            } else if (item.assrolStatus == "D") {
                holder.btnStatus.text = "Done"
            }
            holder.btnAction.visibility = View.VISIBLE
        }

        holder.btnStatus.setOnClickListener { onStatusClick(position) }

        // Ketika tombol action diklik
        holder.btnAction.setOnClickListener { onActionClick(item) }
    }

    override fun getItemCount(): Int = list.size
}