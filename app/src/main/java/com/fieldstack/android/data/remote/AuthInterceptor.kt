package com.fieldstack.android.data.remote

import com.fieldstack.android.util.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val session: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = session.token
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else chain.request()
        val response = chain.proceed(request)
        // Fix #3: 403 means the server rejected the role claim — clear session so the
        // client can't keep retrying with a token the server has already denied.
        if (response.code == 403) session.clear()
        return response
    }
}
