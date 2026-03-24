package com.phantom.util

object CurlGenerator {

    fun generate(
        url: String,
        method: String,
        headers: Map<String, String>,
        body: String?
    ): String {
        val parts = mutableListOf("curl -X $method")

        headers.forEach { (key, value) ->
            parts.add("-H '$key: $value'")
        }

        body?.let {
            val escaped = it.replace("'", "'\\''")
            parts.add("-d '$escaped'")
        }

        parts.add("'$url'")
        return parts.joinToString(" \\\n  ")
    }
}
