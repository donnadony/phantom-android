package com.phantom.core

import android.content.Context
import android.content.SharedPreferences
import com.phantom.model.PhantomMockRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object PhantomMockInterceptor {

    private const val PREFS_NAME = "phantom_mock_rules"
    private const val KEY_RULES = "rules"

    private val _rules = MutableStateFlow<List<PhantomMockRule>>(emptyList())
    val rules: StateFlow<List<PhantomMockRule>> = _rules.asStateFlow()

    private var prefs: SharedPreferences? = null
    private val json = Json { ignoreUnknownKeys = true }

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadRules()
    }

    fun mockResponse(url: String, method: String): Pair<ByteArray, Int>? {
        val rule = _rules.value.firstOrNull { rule ->
            rule.isEnabled && url.contains(rule.url) && rule.method.equals(method, ignoreCase = true)
        } ?: return null

        val response = rule.activeResponse ?: return null
        return Pair(response.body.toByteArray(), response.statusCode)
    }

    fun addRule(rule: PhantomMockRule) {
        _rules.value = _rules.value + rule
        saveRules()
    }

    fun updateRule(updatedRule: PhantomMockRule) {
        _rules.value = _rules.value.map { if (it.id == updatedRule.id) updatedRule else it }
        saveRules()
    }

    fun deleteRule(ruleId: String) {
        _rules.value = _rules.value.filter { it.id != ruleId }
        saveRules()
    }

    fun toggleRule(ruleId: String) {
        _rules.value = _rules.value.map {
            if (it.id == ruleId) it.copy(isEnabled = !it.isEnabled) else it
        }
        saveRules()
    }

    fun cycleResponse(ruleId: String) {
        _rules.value = _rules.value.map { rule ->
            if (rule.id == ruleId && rule.responses.size > 1) {
                val nextIndex = (rule.currentResponseIndex + 1) % rule.responses.size
                rule.copy(currentResponseIndex = nextIndex)
            } else {
                rule
            }
        }
        saveRules()
    }

    private fun loadRules() {
        val raw = prefs?.getString(KEY_RULES, null) ?: return
        _rules.value = runCatching {
            json.decodeFromString<List<PhantomMockRule>>(raw)
        }.getOrDefault(emptyList())
    }

    private fun saveRules() {
        val encoded = json.encodeToString(_rules.value)
        prefs?.edit()?.putString(KEY_RULES, encoded)?.apply()
    }
}
