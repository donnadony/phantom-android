package com.phantom

import com.phantom.core.PhantomMockInterceptor
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class PhantomOkHttpInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val method = request.method

        val mockResult = PhantomMockInterceptor.mockResponse(url, method)
        if (mockResult != null) {
            val (body, statusCode) = mockResult
            Phantom.logRequest(request)
            val mockResponse = Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(statusCode)
                .message("Mocked")
                .body(body.toResponseBody("application/json".toMediaTypeOrNull()))
                .build()
            Phantom.logResponse(request, mockResponse, isMocked = true)
            return mockResponse
        }

        Phantom.logRequest(request)
        val response = chain.proceed(request)
        Phantom.logResponse(request, response)
        return response
    }
}
