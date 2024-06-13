package com.example.myapplication.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body

        // If the request is for the register endpoint and has a form-urlencoded body
        if (request.url.toString().endsWith("register") && requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)

            // Convert request body to string
            val requestBodyString = buffer.readUtf8()

            // Split the request body into parameters
            val params = requestBodyString.split("&")
            for (param in params) {
                // Extract parameter key and value
                val keyValue = param.split("=")
                val key = keyValue[0]
                val value = if (keyValue.size > 1) keyValue[1] else ""

                // Log parameter key and value
                println("Parameter: $key = $value")
            }
        }

        return chain.proceed(request)
    }
}