package com.example.uhf_bt.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String DEFAULT_BASE_URL = "https://skht.my.id/mac.approval_trial/"; // URL default jika belum diatur
    public static final String PREF_API_URL = "api_url";
    private static Retrofit retrofit = null;
    
    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
            
            // Ambil base URL dari SharedPreferences
            String baseUrl = getBaseUrl(context);
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
    
    public static ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }
    
    /**
     * Mengambil base URL dari SharedPreferences
     *
     * @return Base URL yang tersimpan atau URL default jika belum diatur
     */
    public static String getBaseUrl(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String savedUrl = prefs.getString(PREF_API_URL, "");
        
        if (TextUtils.isEmpty(savedUrl)) {
            return DEFAULT_BASE_URL;
        }
        
        // Pastikan URL diakhiri dengan "/"
        if (!savedUrl.endsWith("/")) {
            savedUrl += "/";
        }
        
        return savedUrl;
    }
}