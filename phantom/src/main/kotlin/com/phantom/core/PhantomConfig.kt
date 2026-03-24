package com.phantom.core

import android.content.Context
import android.content.SharedPreferences
import com.phantom.model.PhantomConfigEntry
import com.phantom.model.PhantomConfigType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PhantomConfig {

    private const val PREFS_NAME = "phantom_config"
    private const val KEY_PREFIX = "phantom_config_"

    private val _entries = MutableStateFlow<List<PhantomConfigEntry>>(emptyList())
    val entries: StateFlow<List<PhantomConfigEntry>> = _entries.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun register(
        label: String,
        key: String,
        defaultValue: String,
        type: PhantomConfigType = PhantomConfigType.TEXT,
        options: List<String> = emptyList()
    ) {
        val existing = _entries.value.firstOrNull { it.key == key }
        if (existing != null) return

        val storedValue = prefs?.getString("$KEY_PREFIX$key", null)
        val entry = PhantomConfigEntry(
            label = label,
            key = key,
            defaultValue = defaultValue,
            type = type,
            options = options,
            currentValue = storedValue
        )
        _entries.value = _entries.value + entry
    }

    fun getValue(key: String): String? {
        val entry = _entries.value.firstOrNull { it.key == key } ?: return null
        return entry.resolvedValue
    }

    fun setValue(key: String, value: String?) {
        _entries.value = _entries.value.map { entry ->
            if (entry.key == key) entry.copy(currentValue = value) else entry
        }
        if (value != null) {
            prefs?.edit()?.putString("$KEY_PREFIX$key", value)?.apply()
        } else {
            prefs?.edit()?.remove("$KEY_PREFIX$key")?.apply()
        }
    }

    fun resetValue(key: String) {
        setValue(key, null)
    }
}
