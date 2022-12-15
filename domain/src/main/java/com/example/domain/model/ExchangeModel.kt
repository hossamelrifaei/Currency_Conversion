package com.example.domain.model

data class ExchangeModel(
    var currency: String = "USD",
    val rates: List<ExchangeRate>,
    val resultRates: List<ExchangeRate> = rates
) {
    data class ExchangeRate(val currency: String, val rate: Double)
}

