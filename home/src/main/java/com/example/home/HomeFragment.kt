package com.example.home

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.home.databinding.FragmentCurrenciesBinding
import com.example.lib_ui_common.extensions.makeGone
import com.example.lib_ui_common.extensions.makeVisible
import com.example.lib_ui_common.extensions.onItemSelected
import com.example.lib_ui_common.state.ViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_currencies) {
    private val viewModel: HomeViewModel by viewModels()
    private val currenciesAdapter = CurrenciesAdapter()
    private var _binding: FragmentCurrenciesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCurrenciesBinding.bind(view)

        initUi()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    when (viewState) {
                        ViewState.Error -> {
                            binding.loadingIndicator.makeGone()
                            Toast.makeText(
                            requireContext(),
                            R.string.generic_error_message,
                            Toast.LENGTH_SHORT
                        ).show()}
                        ViewState.Loading -> {
                            binding.loadingIndicator.makeVisible()
                        }
                        is ViewState.Success -> {
                            binding.loadingIndicator.makeGone()
                            if (binding.spCurrencies.adapter == null) {
                                binding.spCurrencies.adapter = context?.let {
                                    ArrayAdapter(
                                        it,
                                        android.R.layout.simple_spinner_item,
                                        viewState.data.rates.map { it.currency })
                                }
                                binding.spCurrencies.setSelection(
                                    viewState.data.rates.indexOfFirst { it.currency == viewState.data.currency }
                                )
                            }
                            currenciesAdapter.submitList(viewState.data.resultRates)
                        }
                    }
                }
            }
        }
    }

    private fun initUi() {
        binding.listCurrencies.adapter = currenciesAdapter
        binding.etAmount.setText(DEFAULT_AMOUNT)
        binding.etAmount.doAfterTextChanged {
            viewModel.onAmountUpdated(it.toString())
        }

        binding.spCurrencies.onItemSelected {
            viewModel.onCurrencySelected(it, binding.etAmount.text.toString())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

    companion object {
        const val DEFAULT_AMOUNT = "1.0"
    }
}


