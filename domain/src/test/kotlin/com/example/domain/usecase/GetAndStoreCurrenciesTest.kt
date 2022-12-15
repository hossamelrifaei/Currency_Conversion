package com.example.domain.usecase

import com.example.data.model.CurrenciesDto
import com.example.data.model.LocalCurrenciesDTO
import com.example.data.repo.CurrencyRepository
import com.example.domain.model.ExchangeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetAndStoreCurrenciesTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var getAndStoreCurrencies: GetAndStoreCurrencies
    private val repository: CurrencyRepository = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        getAndStoreCurrencies = GetAndStoreCurrencies(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should not fetch remote currencies when the local currencies are fresh`() = runTest {
        whenever(repository.shouldFetchRemote()).thenReturn(false)
        val localCurrencies = listOf(
            LocalCurrenciesDTO("USD", 1.0),
            LocalCurrenciesDTO("EUR", 0.9)
        )
        whenever(repository.getLocalCurrencies()).thenReturn(localCurrencies)

        val result = getAndStoreCurrencies()

        assertEquals(
            localCurrencies.map { ExchangeModel.ExchangeRate(it.id, it.rate) },
            result.rates
        )
        verify(repository, never()).getFreshCurrencies()
        verify(repository).shouldFetchRemote()
        verify(repository).getLocalCurrencies()

        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `Should fetch remote currencies when the local currencies are not fresh and store it and update time stamp`() = runTest {
        val remoteExchangeRate = CurrenciesDto(mapOf("USD" to 1.0, "EUR" to 2.0))
        val localExchangeRate = listOf(
            LocalCurrenciesDTO("USD", 1.0),
            LocalCurrenciesDTO("EUR", 2.0)
        )
        whenever(repository.shouldFetchRemote()).thenReturn(true)
        whenever(repository.getFreshCurrencies()).thenReturn(remoteExchangeRate)
        whenever(repository.getLocalCurrencies()).thenReturn(localExchangeRate)

        val result = getAndStoreCurrencies()

        verify(repository).shouldFetchRemote()
        verify(repository).getFreshCurrencies()
        verify(repository).storeLocalCurrencies(localExchangeRate)
        verify(repository).saveTimeStamp()
        verify(repository).getLocalCurrencies()
        assertEquals("USD", result.currency)
        assertEquals(2, result.rates.size)

        verifyNoMoreInteractions(repository)
    }
}