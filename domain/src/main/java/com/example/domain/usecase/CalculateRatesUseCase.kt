package com.example.domain.usecase

import com.example.domain.model.ExchangeModel
import javax.inject.Inject

class CalculateRatesUseCase @Inject constructor() {
    operator fun invoke(
        rates: List<ExchangeModel.ExchangeRate>,
        selectedCurrency: ExchangeModel.ExchangeRate?,
        amount: Double = 1.0
    ): List<ExchangeModel.ExchangeRate> {
        return rates.map { it.copy(rate = (it.rate / (selectedCurrency?.rate ?: 1.0)) * amount) }
    }
}