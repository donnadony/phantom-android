package com.phantom.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object PhantomJson {

    private val json = Json { prettyPrint = true }

    fun prettyPrint(raw: String): String {
        val trimmed = raw.trim()
        return runCatching {
            val element = Json.parseToJsonElement(trimmed)
            json.encodeToString(JsonElement.serializer(), element)
        }.getOrDefault(raw)
    }

    fun isValidJson(raw: String): Boolean {
        val trimmed = raw.trim()
        return runCatching {
            val element = Json.parseToJsonElement(trimmed)
            element is JsonObject || element is JsonArray
        }.getOrDefault(false)
    }
}
