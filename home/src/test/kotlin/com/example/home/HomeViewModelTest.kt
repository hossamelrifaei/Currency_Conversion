package com.example.home

import com.example.domain.model.ExchangeModel
import com.example.domain.usecase.CalculateRatesUseCase
import com.example.domain.usecase.GetAndStoreCurrencies
import com.example.lib_ui_common.state.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class HomeViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private val getAndStoreCurrencies: GetAndStoreCurrencies = mock()
    private val calculateRatesUseCase: CalculateRatesUseCase = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should set the viewstate to error when the getandstorecurrencies throws an exception`() =
        runTest {

            whenever(getAndStoreCurrencies.invoke()).thenThrow(RuntimeException())
            viewModel = HomeViewModel(getAndStoreCurrencies, mock())

            assertEquals(ViewState.Error, viewModel.viewState.first())
            verify(getAndStoreCurrencies).invoke()
            verifyNoMoreInteractions(getAndStoreCurrencies, calculateRatesUseCase)
        }


    @Test
    fun `Should set the viewstate to success when the getandstorecurrencies returns a list of rates`() =
        runTest {
            val rates = listOf(ExchangeModel.ExchangeRate("USD", 1.0))
            whenever(getAndStoreCurrencies.invoke()).thenReturn(ExchangeModel(rates = rates))
            viewModel = HomeViewModel(getAndStoreCurrencies, calculateRatesUseCase)


            assertEquals(
                ViewState.Success(ExchangeModel(rates = rates)),
                viewModel.viewState.first()
            )
            verify(getAndStoreCurrencies).invoke()
            verifyNoMoreInteractions(getAndStoreCurrencies, calculateRatesUseCase)
        }


    @Test
    fun `Should not update the view state when the amount is empty`() = runTest {
        val currency = "USD"
        val rates = listOf(ExchangeModel.ExchangeRate("USD", 1.0))
        val resultRates = listOf(ExchangeModel.ExchangeRate("USD", 1.0))
        val exchangeModel = ExchangeModel(currency, rates, resultRates)

        whenever(getAndStoreCurrencies.invoke()).thenReturn(exchangeModel)
        viewModel = HomeViewModel(getAndStoreCurrencies, calculateRatesUseCase)

        viewModel.onCurrencySelected(0, "")

        assertEquals(
            viewModel.viewState.first(),
            ViewState.Success(exchangeModel)
        )

        verify(getAndStoreCurrencies).invoke()
        verifyNoMoreInteractions(getAndStoreCurrencies, calculateRatesUseCase)
    }

    @Test
    fun `Should update the view state when the amount is a number`() = runTest {

        val amount = "1.0"
        val index = 0
        val currency = "USD"
        val rates = listOf(ExchangeModel.ExchangeRate("USD", 1.0))
        val resultRates = listOf(ExchangeModel.ExchangeRate("USD", 1.0))
        val exchangeModel = ExchangeModel(currency, rates, resultRates)

        whenever(getAndStoreCurrencies.invoke()).thenReturn(exchangeModel)
        whenever(
            calculateRatesUseCase.invoke(
                rates,
                selectedCurrency = ExchangeModel.ExchangeRate("USD", 1.0)
            )
        ).thenReturn(
            listOf(ExchangeModel.ExchangeRate("USD", 1.0))
        )
        viewModel = HomeViewModel(getAndStoreCurrencies, calculateRatesUseCase)

        viewModel.onCurrencySelected(index, amount)

        assertEquals(ViewState.Success(exchangeModel), viewModel.viewState.value)

        verify(getAndStoreCurrencies).invoke()
        verify(calculateRatesUseCase).invoke(any(), any(), any())
        verifyNoMoreInteractions(getAndStoreCurrencies, calculateRatesUseCase)
    }

    @Test
    fun `Should not update the view state when the amount is not a number`() = runTest {
        val currency = "USD"
        val rates = listOf(ExchangeModel.ExchangeRate("USD", 1.0))
        val resultRates = listOf(ExchangeModel.ExchangeRate("USD", 1.0))
        val exchangeModel = ExchangeModel(currency, rates, resultRates)

        whenever(getAndStoreCurrencies.invoke()).thenReturn(exchangeModel)
        whenever(
            calculateRatesUseCase.invoke(
                rates,
                selectedCurrency = ExchangeModel.ExchangeRate("USD", 1.0)
            )
        ).thenReturn(
            listOf(ExchangeModel.ExchangeRate("USD", 1.0))
        )

        viewModel = HomeViewModel(getAndStoreCurrencies, calculateRatesUseCase)

        viewModel.onAmountUpdated("not a number")

        verify(getAndStoreCurrencies).invoke()
        verifyNoMoreInteractions(calculateRatesUseCase, getAndStoreCurrencies)
    }

    /**Should update the view state when the amount is a number*/
    @Test
    fun `should update the view state when the amount is a number`() = runTest {

        val currency = "USD"
        val amount = "2.0"
        val rates = listOf(ExchangeModel.ExchangeRate("USD", 1.0))
        val selectedCurrency = ExchangeModel.ExchangeRate("USD", 1.0)
        val resultRates = listOf(ExchangeModel.ExchangeRate("USD", 1.0))
        val exchangeModel = ExchangeModel(currency, rates, resultRates)
        whenever(getAndStoreCurrencies.invoke()).thenReturn(exchangeModel)
        whenever(calculateRatesUseCase(rates, selectedCurrency, amount.toDouble())).thenReturn(
            resultRates
        )
        viewModel = HomeViewModel(getAndStoreCurrencies, calculateRatesUseCase)

        viewModel.onAmountUpdated(amount)

        assertEquals(ViewState.Success(exchangeModel), viewModel.viewState.value)
        verify(calculateRatesUseCase).invoke(rates, selectedCurrency, amount.toDouble())
        verifyNoMoreInteractions(calculateRatesUseCase)
    }
}