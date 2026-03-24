package com.phantom

import com.phantom.core.PhantomNetworkLogger
import com.phantom.util.PhantomJson
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PhantomNetworkLoggerTest {

    @Before
    fun setup() = runBlocking {
        PhantomNetworkLogger.clear()
    }

    @Test
    fun `log request creates item with defaults`() = runBlocking {
        PhantomNetworkLogger.logRequest(
            url = "https://example.com/api",
            method = "GET",
            headers = emptyMap(),
            body = null
        )

        val items = PhantomNetworkLogger.requests.value
        assertEquals(1, items.size)

        val item = items.first()
        assertEquals("https://example.com/api", item.url)
        assertEquals("GET", item.method)
        assertNull(item.statusCode)
        assertNull(item.responseBody)
    }

    @Test
    fun `log response updates pending request`() = runBlocking {
        PhantomNetworkLogger.logRequest(
            url = "https://example.com/api",
            method = "GET",
            headers = emptyMap(),
            body = null
        )

        PhantomNetworkLogger.logResponse(
            url = "https://example.com/api",
            method = "GET",
            headers = mapOf("Content-Type" to "application/json"),
            body = """{"status":"ok"}""",
            statusCode = 200
        )

        val items = PhantomNetworkLogger.requests.value
        assertEquals(1, items.size)

        val item = items.first()
        assertEquals(200, item.statusCode)
        assertNotNull(item.responseBody)
        assertNotNull(item.duration)
    }

    @Test
    fun `pretty print formats valid json`() {
        val raw = """{"name":"test","value":42}"""
        val pretty = PhantomJson.prettyPrint(raw)

        assertTrue(pretty.contains("\n"))
        assertTrue(pretty.contains("  "))
    }

    @Test
    fun `pretty print returns original for invalid json`() {
        val raw = "not json"
        val result = PhantomJson.prettyPrint(raw)
        assertEquals(raw, result)
    }

    @Test
    fun `pretty print handles json array`() {
        val raw = """[1,2,3]"""
        val pretty = PhantomJson.prettyPrint(raw)

        assertTrue(pretty.contains("\n"))
    }
}
