package com.example.uhf_bt.opname;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uhf_bt.R;

public class OpnameDataActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnAddNew;
    private EditText etStartDate, etToDate, etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opname_data);

        recyclerView = findViewById(R.id.rv_opname_data);
        btnAddNew = findViewById(R.id.btn_add_new);
        etStartDate = findViewById(R.id.et_start_date);
        etToDate = findViewById(R.id.et_to_date);
        etSearch = findViewById(R.id.et_search);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup Adapter dengan data dummy untuk saat ini
        // recyclerView.setAdapter(new OpnameAdapter(listData));
    }
}