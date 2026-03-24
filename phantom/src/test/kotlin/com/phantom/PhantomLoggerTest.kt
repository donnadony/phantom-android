package com.phantom

import com.phantom.core.PhantomLogger
import com.phantom.model.PhantomLogLevel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PhantomLoggerTest {

    @Before
    fun setup() = runBlocking {
        PhantomLogger.clear()
    }

    @Test
    fun `log adds item to the top of the list`() = runBlocking {
        PhantomLogger.log(PhantomLogLevel.INFO, "First message", null)
        PhantomLogger.log(PhantomLogLevel.ERROR, "Second message", null)

        val logs = PhantomLogger.logs.value
        assertEquals(2, logs.size)
        assertEquals("Second message", logs[0].message)
        assertEquals("First message", logs[1].message)
    }

    @Test
    fun `log preserves level and tag`() = runBlocking {
        PhantomLogger.log(PhantomLogLevel.WARNING, "Test", "MyTag")

        val item = PhantomLogger.logs.value.first()
        assertEquals(PhantomLogLevel.WARNING, item.level)
        assertEquals("MyTag", item.tag)
    }

    @Test
    fun `log level emoji values are correct`() {
        assertEquals("\uD83D\uDD0D", PhantomLogLevel.DEBUG.emoji)
        assertEquals("ℹ\uFE0F", PhantomLogLevel.INFO.emoji)
        assertEquals("⚠\uFE0F", PhantomLogLevel.WARNING.emoji)
        assertEquals("❌", PhantomLogLevel.ERROR.emoji)
        assertEquals("\uD83D\uDD25", PhantomLogLevel.CRITICAL.emoji)
    }

    @Test
    fun `log with null tag stores null`() = runBlocking {
        PhantomLogger.log(PhantomLogLevel.INFO, "No tag", null)

        assertNull(PhantomLogger.logs.value.first().tag)
    }

    @Test
    fun `clear removes all logs`() = runBlocking {
        PhantomLogger.log(PhantomLogLevel.INFO, "One", null)
        PhantomLogger.log(PhantomLogLevel.INFO, "Two", null)
        assertEquals(2, PhantomLogger.logs.value.size)

        PhantomLogger.clear()
        assertEquals(0, PhantomLogger.logs.value.size)
    }
}
