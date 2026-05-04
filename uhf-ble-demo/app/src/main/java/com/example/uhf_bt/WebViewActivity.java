package com.example.uhf_bt;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewActivity extends Activity {
    
    private WebView webView;
    private ProgressBar progressBar;
    private TextView tvTitle;
    private ImageButton btnClose;

    private ValueCallback<Uri[]> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        
        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progressBar);
        tvTitle = findViewById(R.id.tvTitle);
        btnClose = findViewById(R.id.btnClose);
        
        // Set click listener untuk tombol close
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Tutup WebViewActivity
            }
        });
        
        // Konfigurasi WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setAllowFileAccess(true); // Tambahkan ini agar bisa akses file
        webView.getSettings().setAllowContentAccess(true); // Tambahkan ini


        // Tambahkan JavaScript Interface untuk komunikasi dengan web
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        
        // Set WebViewClient untuk menangani navigasi dalam WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // Tampilkan loading indicator ketika mulai loading
                progressBar.setVisibility(View.VISIBLE);
                // Update title dengan "Loading..."
                tvTitle.setText("Loading...");
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Sembunyikan loading indicator ketika selesai loading
                progressBar.setVisibility(View.GONE);
                // Update title dengan title halaman web
                String pageTitle = view.getTitle();
                if (pageTitle != null && !pageTitle.isEmpty()) {
                    tvTitle.setText(pageTitle);
                } else {
                    tvTitle.setText("Web View");
                }
            }
            
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // Sembunyikan loading indicator ketika terjadi error
                progressBar.setVisibility(View.GONE);
                // Update title dengan error
                tvTitle.setText("Error");
                Toast.makeText(WebViewActivity.this, "Error loading page: " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.setWebChromeClient(new android.webkit.WebChromeClient() {
            // Untuk Android 5.0+
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                // Pastikan tidak ada proses upload yang sedang berjalan
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = filePathCallback;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*"); // Set ke "image/*" jika hanya ingin gambar

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, intent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Pilih File");

                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                return true;
            }
        });


        // Ambil URL dari Intent
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        
        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        } else {
            Toast.makeText(this, "URL tidak valid", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (mUploadMessage == null) return;

            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    } else if (data.getClipData() != null) {
                        // Jika user memilih banyak file
                        int count = data.getClipData().getItemCount();
                        results = new Uri[count];
                        for (int i = 0; i < count; i++) {
                            results[i] = data.getClipData().getItemAt(i).getUri();
                        }
                    }
                }
            }

            // Kirim data ke WebView
            mUploadMessage.onReceiveValue(results);
            mUploadMessage = null; // Reset callback
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    // JavaScript Interface untuk komunikasi dengan web
    public class WebAppInterface {
        @JavascriptInterface
        public void returnToMainActivity() {
            // Kembali ke MainActivity
            Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        
        @JavascriptInterface
        public void returnToMainActivityWithData(String data) {
            // Kembali ke MainActivity dengan data
            Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
            intent.putExtra("webview_data", data);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}