package com.example.chartapplication.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuoteSymbol(
    @Json(name = "closures")
    val closures: List<Double>,
    @Json(name = "highs")
    val highs: List<Double>,
    @Json(name = "lows")
    val lows: List<Double>,
    @Json(name = "opens")
    val opens: List<Double>,
    @Json(name = "symbol")
    val symbol: String,
    @Json(name = "timestamps")
    val timestamps: List<Int>?,
    @Json(name = "volumes")
    val volumes: List<Int>?
)