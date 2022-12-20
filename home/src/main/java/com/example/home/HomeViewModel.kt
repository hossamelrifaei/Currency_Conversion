package com.example.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.ExchangeModel
import com.example.domain.usecase.CalculateRatesUseCase
import com.example.domain.usecase.GetAndStoreCurrencies
import com.example.lib_ui_common.extensions.toDoubleOrZero
import com.example.lib_ui_common.state.ViewState
import com.example.lib_ui_common.state.ViewState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAndStoreCurrencies: GetAndStoreCurrencies,
    private val calculateRatesUseCase: CalculateRatesUseCase
) : ViewModel() {

    private val _viewState =
        MutableStateFlow<ViewState<ExchangeModel>>(
            Loading
        )
    val viewState: StateFlow<ViewState<ExchangeModel>> =
        _viewState

    init {
        loadRates()
    }

    private fun loadRates() {
        _viewState.value = Loading
        viewModelScope.launch {
            runCatching {
                getAndStoreCurrencies()
            }.onFailure {
                _viewState.value = Error
            }.onSuccess { rates ->
                _viewState.value = Success(rates)
            }
        }
    }

    fun onAmountUpdated(amount: String) {
        _viewState.value = _viewState.value.let { state ->
            when (state) {
                Error -> state
                Loading -> state
                is Success -> state.copy(
                    data = state.data.copy(
                        resultRates = calculateRatesUseCase(
                            state.data.rates,
                            state.data.rates.firstOrNull { it.currency == state.data.currency },
                            amount.toDoubleOrZero()
                        )
                    )
                )
            }
        }
    }

    fun onCurrencySelected(index: Int, amount: String) {
        _viewState.value = _viewState.value.let { state ->
            when (state) {
                Error -> state
                Loading -> state
                is Success -> state.copy(
                    data = state.data.copy(
                        currency = state.data.rates[index].currency,
                        resultRates = calculateRatesUseCase(
                            state.data.rates,
                            state.data.rates[index],
                            amount.toDoubleOrZero()
                        )
                    )
                )
            }
        }
    }
}