package com.example.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.ExchangeModel.ExchangeRate
import com.example.home.databinding.CurrencyItemBinding
import com.example.lib_ui_common.extensions.roundDoubleToString

class CurrencyListViewHolder(private val binding: CurrencyItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ExchangeRate) {
        binding.tvCurrencyCode.text = item.currency
        binding.tvAmount.text = item.rate.roundDoubleToString()
    }

    companion object {
        fun create(
            parent: ViewGroup
        ): CurrencyListViewHolder {
            return CurrencyListViewHolder(
                CurrencyItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

}