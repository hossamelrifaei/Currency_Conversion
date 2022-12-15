package com.example.domain.usecase

import com.example.domain.model.ExchangeModel
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateRatesUseCaseTest {

    @Test
    fun `Should return the same list when selectedcurrency is null`() {
        val rates = listOf(
            ExchangeModel.ExchangeRate("USD", 1.0),
            ExchangeModel.ExchangeRate("EUR", 2.0)
        )
        val useCase = CalculateRatesUseCase()
        val result = useCase(rates, null)
        assertEquals(rates, result)
    }


    @Test
    fun `Should return the same list when amount is 1`() {
        val rates = listOf(
            ExchangeModel.ExchangeRate("USD", 1.0),
            ExchangeModel.ExchangeRate("EUR", 0.9)
        )
        val selectedCurrency = ExchangeModel.ExchangeRate("USD", 1.0)
        val amount = 1.0

        val result = CalculateRatesUseCase().invoke(rates, selectedCurrency, amount)

        assertEquals(rates, result)
    }


    @Test
    fun `Should return the list with rate equals to 2 when selectedcurrency is not null and amount is 2`() {
        val rates = listOf(
            ExchangeModel.ExchangeRate("USD", 1.0),
            ExchangeModel.ExchangeRate("EUR", 2.0)
        )
        val selectedCurrency = ExchangeModel.ExchangeRate("USD", 1.0)
        val amount = 2.0
        val expectedResult = listOf(
            ExchangeModel.ExchangeRate("USD", 2.0),
            ExchangeModel.ExchangeRate("EUR", 4.0)
        )

        val result = CalculateRatesUseCase().invoke(rates, selectedCurrency, amount)

        assertEquals(expectedResult, result)
    }


    @Test
    fun `Should return the list with rate equals to 1 when selectedcurrency is not null and amount is 1`() {
        val rates = listOf(
            ExchangeModel.ExchangeRate("USD", 1.0),
            ExchangeModel.ExchangeRate("EUR", 2.0)
        )
        val selectedCurrency = ExchangeModel.ExchangeRate("USD", 1.0)
        val amount = 1.0
        val expectedResult = listOf(
            ExchangeModel.ExchangeRate("USD", 1.0),
            ExchangeModel.ExchangeRate("EUR", 2.0)
        )

        val result = CalculateRatesUseCase().invoke(rates, selectedCurrency, amount)

        assertEquals(expectedResult, result)
    }
}