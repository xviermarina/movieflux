package com.mxvier.core.network

import com.mxvier.core.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiToken = BuildConfig.TMDB_API_KEY

        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $apiToken")
            .header("accept", "application/json")
            .build()

        return chain.proceed(newRequest)
    }
}