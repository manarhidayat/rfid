package com.example.uhf_bt.opname

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uhf_bt.R
import com.example.uhf_bt.model.Opname

class OpnameDataActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var btnAddNew: Button? = null
    private var etStartDate: EditText? = null
    private var etToDate: EditText? = null
    private var etSearch: EditText? = null

    private var adapter: OpnameAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opname_data)

        recyclerView = findViewById(R.id.rv_opname_data)
        btnAddNew = findViewById(R.id.btn_add_new)
        etStartDate = findViewById(R.id.et_start_date)
        etToDate = findViewById(R.id.et_to_date)
        etSearch = findViewById(R.id.et_search)

        recyclerView!!.setLayoutManager(LinearLayoutManager(this))

        // 3. Setup RecyclerView & Data Dummy
        setupRecyclerView()

        btnAddNew!!.setOnClickListener(View.OnClickListener { view: View? ->
            val intent = Intent(this@OpnameDataActivity, OpnameAddActivity::class.java)
            startActivity(intent)
        })
    }

    private fun setupRecyclerView() {
        recyclerView!!.setLayoutManager(LinearLayoutManager(this))

        // Membuat data dummy
        val dummyList: MutableList<Opname> = ArrayList()
        dummyList.add(Opname("1", "OPN-20231001-001", "2023-10-01"))
        dummyList.add(Opname("2", "OPN-20231002-005", "2023-10-02"))
        dummyList.add(Opname("3", "OPN-20231005-012", "2023-10-05"))

        // Pasang ke adapter (Ganti OpnameAdapter dengan nama class adapter Anda)
        // Inisialisasi adapter dengan listener
        val adapter = OpnameAdapter(dummyList) { opname ->
            // Logika ketika item diklik
            val intent = Intent(this, OpnameLocationActivity::class.java)

            // Membawa data objek Opname
            intent.putExtra("EXTRA_OPNAME", opname)

            startActivity(intent)
        }
        recyclerView!!.setAdapter(adapter)
    }
}