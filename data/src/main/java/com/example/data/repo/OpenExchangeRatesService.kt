package com.example.data.repo

import com.example.data.model.CurrenciesDto
import retrofit2.http.GET

interface OpenExchangeRatesService {
    @GET("api/latest.json")
    suspend fun getCurrencies(): CurrenciesDto
}