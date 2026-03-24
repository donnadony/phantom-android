package com.phantom.core

import com.phantom.model.PhantomNetworkItem
import com.phantom.util.CurlGenerator
import com.phantom.util.PhantomJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object PhantomNetworkLogger {

    private val _requests = MutableStateFlow<List<PhantomNetworkItem>>(emptyList())
    val requests: StateFlow<List<PhantomNetworkItem>> = _requests.asStateFlow()

    private val pendingRequests = mutableMapOf<String, PhantomNetworkItem>()
    private val mutex = Mutex()

    private fun compositeKey(url: String, method: String): String {
        return "$method|$url"
    }

    suspend fun logRequest(
        url: String,
        method: String,
        headers: Map<String, String>,
        body: String?
    ) {
        mutex.withLock {
            val curl = CurlGenerator.generate(url, method, headers, body)
            val item = PhantomNetworkItem(
                url = url,
                method = method,
                requestHeaders = headers,
                requestBody = body?.let { PhantomJson.prettyPrint(it) },
                curlCommand = curl
            )
            val key = compositeKey(url, method)
            pendingRequests[key] = item
            _requests.value = listOf(item) + _requests.value
        }
    }

    suspend fun logResponse(
        url: String,
        method: String,
        headers: Map<String, String>,
        body: String?,
        statusCode: Int?
    ) {
        mutex.withLock {
            val key = compositeKey(url, method)
            val pending = pendingRequests.remove(key)
            val duration = pending?.let { System.currentTimeMillis() - it.timestamp }

            val updatedItem = (pending ?: PhantomNetworkItem(url = url, method = method)).copy(
                responseHeaders = headers,
                responseBody = body?.let { PhantomJson.prettyPrint(it) },
                statusCode = statusCode,
                duration = duration
            )

            _requests.value = _requests.value.map { item ->
                if (item.id == updatedItem.id) updatedItem else item
            }.let { list ->
                if (list.any { it.id == updatedItem.id }) list
                else listOf(updatedItem) + list
            }
        }
    }

    fun logRequestSync(
        url: String,
        method: String,
        headers: Map<String, String>,
        body: String?
    ) {
        val curl = CurlGenerator.generate(url, method, headers, body)
        val item = PhantomNetworkItem(
            url = url,
            method = method,
            requestHeaders = headers,
            requestBody = body?.let { PhantomJson.prettyPrint(it) },
            curlCommand = curl
        )
        val key = compositeKey(url, method)
        pendingRequests[key] = item
        _requests.value = listOf(item) + _requests.value
    }

    fun logResponseSync(
        url: String,
        method: String,
        headers: Map<String, String>,
        body: String?,
        statusCode: Int?
    ) {
        val key = compositeKey(url, method)
        val pending = pendingRequests.remove(key)
        val duration = pending?.let { System.currentTimeMillis() - it.timestamp }

        val updatedItem = (pending ?: PhantomNetworkItem(url = url, method = method)).copy(
            responseHeaders = headers,
            responseBody = body?.let { PhantomJson.prettyPrint(it) },
            statusCode = statusCode,
            duration = duration
        )

        _requests.value = _requests.value.map { item ->
            if (item.id == updatedItem.id) updatedItem else item
        }.let { list ->
            if (list.any { it.id == updatedItem.id }) list
            else listOf(updatedItem) + list
        }
    }

    suspend fun clear() {
        mutex.withLock {
            pendingRequests.clear()
            _requests.value = emptyList()
        }
    }
}
