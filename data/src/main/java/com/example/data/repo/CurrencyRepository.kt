package com.example.data.repo

import android.content.SharedPreferences
import com.example.data.model.CurrenciesDto
import com.example.data.model.LocalCurrenciesDTO
import database.CurrenciesDataBase
import javax.inject.Inject

const val TIME_STAMP_KEY = "TimestampKey"
const val TIME_TO_UPDATE = 30 * 60 * 1000


class CurrencyRepository @Inject constructor(
    private val openExchangeRatesService: OpenExchangeRatesService,
    private val sharedPreferences: SharedPreferences,
    private val currenciesDataBase: CurrenciesDataBase
) {
    suspend fun getFreshCurrencies(): CurrenciesDto = openExchangeRatesService.getCurrencies()

    suspend fun getLocalCurrencies(): List<LocalCurrenciesDTO> =
        currenciesDataBase.currenciesDao().getLocalCurrencies()

    suspend fun storeLocalCurrencies(rates: List<LocalCurrenciesDTO>) {
        currenciesDataBase.currenciesDao().insertRates(rates)
    }

    fun shouldFetchRemote(): Boolean {
        val timestamp = sharedPreferences.getLong(TIME_STAMP_KEY, 0)
        val currentTime = System.currentTimeMillis()
        return currentTime - timestamp > TIME_TO_UPDATE
    }

    fun saveTimeStamp() =
        sharedPreferences.edit().putLong(TIME_STAMP_KEY, System.currentTimeMillis()).apply()

}