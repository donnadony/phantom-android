package com.phantom.model

import java.util.UUID

data class PhantomLogItem(
    val id: String = UUID.randomUUID().toString(),
    val level: PhantomLogLevel,
    val message: String,
    val tag: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
