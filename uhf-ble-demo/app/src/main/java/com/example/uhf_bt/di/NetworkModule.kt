package com.example.uhf_bt.di

import android.content.Context
import android.preference.PreferenceManager
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
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
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
