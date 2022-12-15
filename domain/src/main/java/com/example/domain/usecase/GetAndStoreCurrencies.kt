package com.example.domain.usecase

import com.example.data.model.LocalCurrenciesDTO
import com.example.data.repo.CurrencyRepository
import com.example.domain.model.ExchangeModel
import javax.inject.Inject

class GetAndStoreCurrencies @Inject constructor(private val repository: CurrencyRepository) {
    suspend operator fun invoke(): ExchangeModel {
        if (repository.shouldFetchRemote()) {
            val remoteExchangeRate = repository.getFreshCurrencies()
            repository.storeLocalCurrencies(remoteExchangeRate.rates.map {
                LocalCurrenciesDTO(it.key, it.value)
            })
            repository.saveTimeStamp()
        }
        return ExchangeModel(rates = repository.getLocalCurrencies().map {
            ExchangeModel.ExchangeRate(
                it.id,
                it.rate,
            )
        })
    }
}