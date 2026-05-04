package com.example.uhf_bt.di

import android.content.Context
import android.preference.PreferenceManager
import com.example.uhf_bt.LoginActivity
import com.example.uhf_bt.api.ApiClient
import com.example.uhf_bt.api.ApiClient.DEFAULT_BASE_URL
import com.example.uhf_bt.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @ApplicationContext context: Context // Tambahkan context di sini
    ): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                // Ambil token yang disimpan di LoginActivity (sesuaikan key-nya, misal "token")

                // Ambil token dari LoginActivity
                val token = LoginActivity.getToken(context)

                val requestBuilder = original.newBuilder()
                if (!token.isNullOrEmpty()) {
                    // Tambahkan header Authorization
                    requestBuilder.header("Authorization", "Bearer $token")
                }

                val request = requestBuilder.method(original.method, original.body).build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): Retrofit {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        val savedUrl = sharedPreferences.getString(ApiClient.PREF_API_URL, DEFAULT_BASE_URL)
            ?: DEFAULT_BASE_URL

        // Pastikan URL diakhiri dengan '/' karena Retrofit mewajibkannya
        val finalUrl = if (savedUrl.endsWith("/")) savedUrl else "$savedUrl/"

        return Retrofit.Builder()
            .baseUrl(finalUrl) // Ganti dengan Base URL Anda
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


}
