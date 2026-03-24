package com.phantom

import android.content.Context
import android.content.Intent
import com.phantom.core.PhantomConfig
import com.phantom.core.PhantomLogger
import com.phantom.core.PhantomMockInterceptor
import com.phantom.core.PhantomNetworkLogger
import com.phantom.model.PhantomConfigType
import com.phantom.model.PhantomLogLevel
import com.phantom.theme.PhantomTheme
import com.phantom.ui.PhantomActivity
import okhttp3.Request
import okhttp3.Response
import okio.Buffer

object Phantom {

    var currentTheme: PhantomTheme = PhantomTheme.default
        private set

    fun init(context: Context) {
        val appContext = context.applicationContext
        PhantomMockInterceptor.init(appContext)
        PhantomConfig.init(appContext)
    }

    fun setTheme(theme: PhantomTheme) {
        currentTheme = theme
    }

    fun log(
        level: PhantomLogLevel = PhantomLogLevel.INFO,
        message: String,
        tag: String? = null
    ) {
        PhantomLogger.logSync(level, message, tag)
    }

    fun logRequest(
        url: String,
        method: String,
        headers: Map<String, String> = emptyMap(),
        body: String? = null
    ) {
        PhantomNetworkLogger.logRequestSync(url, method, headers, body)
    }

    fun logResponse(
        url: String,
        method: String,
        headers: Map<String, String> = emptyMap(),
        body: String? = null,
        statusCode: Int? = null,
        isMocked: Boolean = false
    ) {
        PhantomNetworkLogger.logResponseSync(url, method, headers, body, statusCode, isMocked)
    }

    fun logRequest(request: Request) {
        val headers = request.headers.toMap()
        val bodyString = request.body?.let { body ->
            runCatching {
                val buffer = Buffer()
                body.writeTo(buffer)
                buffer.readUtf8()
            }.getOrNull()
        }
        logRequest(request.url.toString(), request.method, headers, bodyString)
    }

    fun logResponse(request: Request, response: Response, isMocked: Boolean = false) {
        val headers = response.headers.toMap()
        val bodyString = runCatching {
            response.peekBody(1024 * 1024).string()
        }.getOrNull()
        logResponse(request.url.toString(), request.method, headers, bodyString, response.code, isMocked)
    }

    fun mockResponse(url: String, method: String): Pair<ByteArray, Int>? {
        return PhantomMockInterceptor.mockResponse(url, method)
    }

    fun registerConfig(
        label: String,
        key: String,
        defaultValue: String,
        type: PhantomConfigType = PhantomConfigType.TEXT,
        options: List<String> = emptyList()
    ) {
        PhantomConfig.register(label, key, defaultValue, type, options)
    }

    fun config(key: String): String? {
        return PhantomConfig.getValue(key)
    }

    fun setConfig(key: String, value: String?) {
        PhantomConfig.setValue(key, value)
    }

    fun show(context: Context) {
        val intent = Intent(context, PhantomActivity::class.java)
        context.startActivity(intent)
    }

    private fun okhttp3.Headers.toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (i in 0 until size) {
            map[name(i)] = value(i)
        }
        return map
    }
}
