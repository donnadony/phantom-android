package com.phantom.model

enum class PhantomLogLevel(val emoji: String, val label: String) {
    DEBUG("🔍", "DEBUG"),
    INFO("ℹ️", "INFO"),
    WARNING("⚠️", "WARNING"),
    ERROR("❌", "ERROR"),
    CRITICAL("🔥", "CRITICAL");
}
