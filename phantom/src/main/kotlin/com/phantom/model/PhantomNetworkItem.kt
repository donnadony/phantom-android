package com.phantom.model

import java.util.UUID

data class PhantomNetworkItem(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val method: String,
    val requestHeaders: Map<String, String> = emptyMap(),
    val requestBody: String? = null,
    val responseHeaders: Map<String, String> = emptyMap(),
    val responseBody: String? = null,
    val statusCode: Int? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val duration: Long? = null,
    val curlCommand: String? = null,
    val isMocked: Boolean = false
) {
    val responseSize: String
        get() {
            val bytes = responseBody?.toByteArray()?.size ?: 0
            return when {
                bytes < 1024 -> "$bytes B"
                bytes < 1024 * 1024 -> "${bytes / 1024} KB"
                else -> "${bytes / (1024 * 1024)} MB"
            }
        }

    val isError: Boolean
        get() = statusCode != null && statusCode >= 400

    val isSlow: Boolean
        get() = duration != null && duration > 1000
}
