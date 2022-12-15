package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrenciesDto(
    @Json(name = "rates") val rates: Map<String, Double>
)