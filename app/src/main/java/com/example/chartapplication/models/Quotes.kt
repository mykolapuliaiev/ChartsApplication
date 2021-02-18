package com.example.chartapplication.models
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


@JsonClass(generateAdapter = true)
data class Quotes(
    @Json(name = "content")
    val content: QuotesContent?,
    @Json(name = "status")
    val status: String?
)
