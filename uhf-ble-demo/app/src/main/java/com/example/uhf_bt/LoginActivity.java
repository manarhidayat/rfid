package com.example.uhf_bt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uhf_bt.api.ApiClient;
import com.example.uhf_bt.model.LoginRequest;
import com.example.uhf_bt.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSetupUrl;
    private View progressOverlay;
    
    private static final String PREF_API_URL = "api_url";
    private static final String PREF_TOKEN = "auth_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Cek apakah user sudah login sebelumnya
        if (checkExistingToken()) {
            // Jika token ada dan valid, langsung ke MainActivity
            redirectToMainActivity();
            return;
        }
        
        // Inisialisasi UI
        initUI();
        
        // Set listener untuk tombol login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void initUI() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSetupUrl = findViewById(R.id.btnSetupUrl);
        progressOverlay = findViewById(R.id.progress_overlay);
        
        // Jika progress_overlay belum ada di layout, kita bisa menambahkannya secara programatis
        if (progressOverlay == null) {
            // Gunakan metode showToast untuk menampilkan loading sebagai alternatif
        }
        
        // Set listener untuk tombol setup URL
        btnSetupUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetupUrlDialog();
            }
        });
    }

    private void login() {
        // Ambil nilai dari input
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        // Validasi input
        if (TextUtils.isEmpty(username)) {
            showToast("Username tidak boleh kosong");
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            showToast("Password tidak boleh kosong");
            return;
        }
        
        // Tampilkan loading
        showLoading("Logging in...");
        
        // Buat request login
        LoginRequest loginRequest = new LoginRequest(username, password);
        
        // Panggil API login
        Call<LoginResponse> call = ApiClient.getApiService(LoginActivity.this).login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Sembunyikan loading
                hideLoading();

                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.getStatus() == 200) {
                        // Login berhasil
                        showToast(loginResponse.getMessage() + " kcoak");

                        // Simpan token jika diperlukan
                        saveToken(loginResponse.getToken());

                        // Buka MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Tutup LoginActivity agar tidak bisa kembali dengan tombol back
                    } else {
                        // Login gagal dengan pesan dari server
                        showToast(loginResponse != null ? loginResponse.getMessage() : "Login gagal");
                    }
                } else {
                    // Error response dari server
                    showToast("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("wew", "masuk");
                // Sembunyikan loading
                hideLoading();

                // Error koneksi atau exception lainnya
                showToast("Error: " + t.getMessage());
            }
        });
    }
    
    private void showLoading(String message) {
        if (progressOverlay != null) {
            progressOverlay.setVisibility(View.VISIBLE);
        } else {
            // Gunakan Toast sebagai alternatif jika overlay tidak tersedia
            showToast(message);
        }
    }
    
    private void hideLoading() {
        if (progressOverlay != null) {
            progressOverlay.setVisibility(View.GONE);
        }
    }
    
    /**
     * Menampilkan dialog untuk setup URL API
     */
    private void showSetupUrlDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_setup_url, null);
        builder.setView(dialogView);
        
        final EditText etApiUrl = dialogView.findViewById(R.id.etApiUrl);
        
        // Ambil URL yang tersimpan (jika ada)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String savedUrl = prefs.getString(PREF_API_URL, "");
        if (!TextUtils.isEmpty(savedUrl)) {
            etApiUrl.setText(savedUrl);
        }
        
        builder.setPositiveButton("Save", (dialog, which) -> {
            String apiUrl = etApiUrl.getText().toString().trim();
            if (!TextUtils.isEmpty(apiUrl)) {
                // Pastikan URL diakhiri dengan "/"
                if (!apiUrl.endsWith("/")) {
                    apiUrl += "/";
                }
                
                // Simpan URL ke SharedPreferences
                saveApiUrl(apiUrl);
                showToast("API URL saved: " + apiUrl);
            } else {
                showToast("URL cannot be empty");
            }
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    /**
     * Menyimpan URL API ke SharedPreferences
     * @param url URL API yang akan disimpan
     */
    private void saveApiUrl(String url) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_API_URL, url);
        editor.apply();
    }
    
    /**
     * Menyimpan token autentikasi ke SharedPreferences
     * @param token Token autentikasi yang akan disimpan
     */
    private void saveToken(String token) {
        if (!TextUtils.isEmpty(token)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_TOKEN, token);
            editor.apply();
        }
    }
    
    /**
     * Mengambil token autentikasi dari SharedPreferences
     * @return Token autentikasi yang tersimpan atau null jika tidak ada
     */
    public static String getToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_TOKEN, null);
    }
    
    /**
     * Menghapus token autentikasi dari SharedPreferences (untuk logout)
     */
    public static void clearToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PREF_TOKEN);
        editor.apply();
    }
    
    /**
     * Mengecek apakah token yang tersimpan masih valid
     * @return true jika token ada dan tidak kosong, false jika tidak ada atau kosong
     */
    private boolean checkExistingToken() {
        String token = getToken(this);
        return !TextUtils.isEmpty(token);
    }
    
    /**
     * Redirect ke MainActivity tanpa menampilkan UI login
     */
    private void redirectToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Tutup LoginActivity agar tidak bisa kembali dengan tombol back
    }
}