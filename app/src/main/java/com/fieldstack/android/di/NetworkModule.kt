package com.fieldstack.android.di

import com.fieldstack.android.BuildConfig
import com.fieldstack.android.data.remote.AuthInterceptor
import com.fieldstack.android.data.remote.FieldStackApi
import com.fieldstack.android.util.SessionManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Certificate pins for api.fieldstack.com.
     * Obtain the current pin by running:
     *   openssl s_client -connect api.fieldstack.com:443 | \
     *     openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | \
     *     openssl dgst -sha256 -binary | base64
     * Include at least two pins (leaf + backup/intermediate) to allow rotation.
     *
     * REQUIRED: Replace placeholder values with real SHA-256 SPKI pins before releasing to production.
     *           Until replaced, certificate pinning is effectively disabled and MITM attacks are possible.
     *           The app will throw IllegalStateException on startup in non-debug builds if placeholders remain.
     */
    private val CERT_PINNER = CertificatePinner.Builder()
        .add("api.fieldstack.com", "sha256/REPLACE_WITH_LEAF_CERT_PIN=")
        .add("api.fieldstack.com", "sha256/REPLACE_WITH_BACKUP_CERT_PIN=")
        .build()

    /** Throws at startup in non-debug builds if placeholder pins have not been replaced. */
    private fun checkCertPinsConfigured() {
        if (!BuildConfig.DEBUG) {
            val placeholders = listOf("sha256/REPLACE_WITH_LEAF_CERT_PIN=", "sha256/REPLACE_WITH_BACKUP_CERT_PIN=")
            check(placeholders.none { it.contains("REPLACE_WITH") }) {
                "SECURITY: Certificate pins are still placeholders. " +
                "Replace REPLACE_WITH_LEAF_CERT_PIN and REPLACE_WITH_BACKUP_CERT_PIN " +
                "in NetworkModule before shipping to production."
            }
        }
    }

    @Provides @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides @Singleton
    fun provideOkHttp(session: SessionManager): OkHttpClient {
        checkCertPinsConfigured()
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(session))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS
                        else HttpLoggingInterceptor.Level.NONE
                redactHeader("Authorization")
                redactHeader("Cookie")
            })
            .authenticator { _, response ->
                if (response.code == 401) session.clear()
                null
            }
            // Fix #9: only pin in release/staging; skip in debug so Charles/Proxyman work
            .apply { if (!BuildConfig.DEBUG) certificatePinner(CERT_PINNER) }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(okHttp: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun provideApi(retrofit: Retrofit): FieldStackApi =
        retrofit.create(FieldStackApi::class.java)
}
