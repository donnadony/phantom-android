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
    val curlCommand: String? = null
)
