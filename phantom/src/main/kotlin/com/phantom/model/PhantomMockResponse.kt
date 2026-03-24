package com.phantom.model

import kotlinx.serialization.Serializable

@Serializable
data class PhantomMockResponse(
    val statusCode: Int = 200,
    val body: String = "{}"
)
