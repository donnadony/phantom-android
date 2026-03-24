package com.phantom.model

data class PhantomConfigEntry(
    val label: String,
    val key: String,
    val defaultValue: String,
    val type: PhantomConfigType = PhantomConfigType.TEXT,
    val options: List<String> = emptyList(),
    val currentValue: String? = null
) {
    val resolvedValue: String
        get() = currentValue ?: defaultValue

    val isModified: Boolean
        get() = currentValue != null && currentValue != defaultValue
}
