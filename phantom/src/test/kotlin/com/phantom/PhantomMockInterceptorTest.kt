package com.phantom

import com.phantom.core.PhantomMockInterceptor
import com.phantom.model.PhantomMockResponse
import com.phantom.model.PhantomMockRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PhantomMockInterceptorTest {

    @Before
    fun setup() {
        PhantomMockInterceptor.rules.value.forEach { rule ->
            PhantomMockInterceptor.deleteRule(rule.id)
        }
    }

    @Test
    fun `mockResponse returns matching rule`() {
        val rule = PhantomMockRule(
            url = "/api/users",
            method = "GET",
            responses = listOf(PhantomMockResponse(statusCode = 200, body = """{"ok":true}"""))
        )
        PhantomMockInterceptor.addRule(rule)

        val result = PhantomMockInterceptor.mockResponse("https://example.com/api/users", "GET")
        assertNotNull(result)
        assertEquals(200, result!!.second)
        assertEquals("""{"ok":true}""", String(result.first))
    }

    @Test
    fun `mockResponse returns null for non-matching method`() {
        val rule = PhantomMockRule(url = "/api/users", method = "GET")
        PhantomMockInterceptor.addRule(rule)

        val result = PhantomMockInterceptor.mockResponse("https://example.com/api/users", "POST")
        assertNull(result)
    }

    @Test
    fun `mockResponse returns null for non-matching url`() {
        val rule = PhantomMockRule(url = "/api/users", method = "GET")
        PhantomMockInterceptor.addRule(rule)

        val result = PhantomMockInterceptor.mockResponse("https://example.com/api/posts", "GET")
        assertNull(result)
    }

    @Test
    fun `disabled rule is not matched`() {
        val rule = PhantomMockRule(
            url = "/api/users",
            method = "GET",
            isEnabled = false
        )
        PhantomMockInterceptor.addRule(rule)

        val result = PhantomMockInterceptor.mockResponse("https://example.com/api/users", "GET")
        assertNull(result)
    }

    @Test
    fun `toggle rule changes enabled state`() {
        val rule = PhantomMockRule(url = "/api/users", method = "GET")
        PhantomMockInterceptor.addRule(rule)

        PhantomMockInterceptor.toggleRule(rule.id)
        val toggled = PhantomMockInterceptor.rules.value.first()
        assertEquals(false, toggled.isEnabled)

        PhantomMockInterceptor.toggleRule(rule.id)
        val restored = PhantomMockInterceptor.rules.value.first()
        assertEquals(true, restored.isEnabled)
    }

    @Test
    fun `delete rule removes it`() {
        val rule = PhantomMockRule(url = "/api/users", method = "GET")
        PhantomMockInterceptor.addRule(rule)
        assertEquals(1, PhantomMockInterceptor.rules.value.size)

        PhantomMockInterceptor.deleteRule(rule.id)
        assertEquals(0, PhantomMockInterceptor.rules.value.size)
    }

    @Test
    fun `cycle response advances index`() {
        val rule = PhantomMockRule(
            url = "/api/users",
            method = "GET",
            responses = listOf(
                PhantomMockResponse(statusCode = 200, body = "first"),
                PhantomMockResponse(statusCode = 500, body = "second")
            )
        )
        PhantomMockInterceptor.addRule(rule)

        assertEquals(0, PhantomMockInterceptor.rules.value.first().currentResponseIndex)

        PhantomMockInterceptor.cycleResponse(rule.id)
        assertEquals(1, PhantomMockInterceptor.rules.value.first().currentResponseIndex)

        PhantomMockInterceptor.cycleResponse(rule.id)
        assertEquals(0, PhantomMockInterceptor.rules.value.first().currentResponseIndex)
    }
}
