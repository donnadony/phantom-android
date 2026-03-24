package com.phantom.core

import com.phantom.model.PhantomLogItem
import com.phantom.model.PhantomLogLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object PhantomLogger {

    private val _logs = MutableStateFlow<List<PhantomLogItem>>(emptyList())
    val logs: StateFlow<List<PhantomLogItem>> = _logs.asStateFlow()

    private val mutex = Mutex()

    suspend fun log(level: PhantomLogLevel, message: String, tag: String?) {
        mutex.withLock {
            val item = PhantomLogItem(
                level = level,
                message = message,
                tag = tag
            )
            _logs.value = listOf(item) + _logs.value
        }
    }

    fun logSync(level: PhantomLogLevel, message: String, tag: String?) {
        val item = PhantomLogItem(
            level = level,
            message = message,
            tag = tag
        )
        _logs.value = listOf(item) + _logs.value
    }

    suspend fun clear() {
        mutex.withLock {
            _logs.value = emptyList()
        }
    }
}
