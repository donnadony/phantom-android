package com.phantom.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PhantomMockRule(
    val id: String = UUID.randomUUID().toString(),
    val description: String = "",
    val url: String,
    val method: String = "GET",
    val isEnabled: Boolean = true,
    val responses: List<PhantomMockResponse> = listOf(PhantomMockResponse()),
    val currentResponseIndex: Int = 0
) {
    val activeResponse: PhantomMockResponse?
        get() = responses.getOrNull(currentResponseIndex)
}
