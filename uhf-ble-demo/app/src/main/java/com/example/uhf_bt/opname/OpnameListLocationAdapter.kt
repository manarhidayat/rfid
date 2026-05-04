package com.example.uhf_bt.opname

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.AssetOpname

class OpnameListLocationAdapter(private val list: List<AssetOpname>) :
    RecyclerView.Adapter<OpnameListLocationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNo: TextView = view.findViewById(R.id.item_tv_no)
        val tvCode: TextView = view.findViewById(R.id.item_tv_asset_code)
        val tvName: TextView = view.findViewById(R.id.item_tv_asset_name)
        val tvStatus: TextView = view.findViewById(R.id.item_tv_status)
        val tvPrevLoc: TextView = view.findViewById(R.id.item_tv_prev_loc)
        val btnAction: Button = view.findViewById(R.id.item_btn_action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_opname_list_location, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvNo.text = "${position + 1}"
        holder.tvCode.text = item.assRfidCode
        holder.tvName.text = item.assDesc
        holder.tvStatus.text = ""

        // Warna status
        if(!item.status.isNullOrEmpty()) {
            when (item.status) {
                "1" -> {
                    holder.tvStatus.text = "Found"
                    holder.tvPrevLoc.visibility = View.GONE
                    holder.tvStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            android.R.color.holo_green_dark
                        )
                    )
                }
                "2" -> {
                    holder.tvStatus.text = "Not Found"
                    holder.tvPrevLoc.visibility = View.GONE
                    holder.tvStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            android.R.color.holo_red_dark
                        )
                    )
                }
                "3" -> {
                    holder.tvStatus.text = "Foreign"
                    holder.tvPrevLoc.visibility = View.VISIBLE
                    holder.tvPrevLoc.text = item.assLocId
                    holder.tvStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            android.R.color.holo_orange_dark
                        )
                    )
                }
            }
        }

        holder.btnAction.setOnClickListener {
            // Logika Action
        }
    }

    override fun getItemCount(): Int = list.size
}