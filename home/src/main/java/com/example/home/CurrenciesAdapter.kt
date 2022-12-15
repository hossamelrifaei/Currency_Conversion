package com.example.home

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.domain.model.ExchangeModel.ExchangeRate

class CurrenciesAdapter : ListAdapter<ExchangeRate, CurrencyListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyListViewHolder {
        return CurrencyListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CurrencyListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ExchangeRate>() {
            override fun areItemsTheSame(oldItem: ExchangeRate, newItem: ExchangeRate): Boolean {
                return oldItem.currency == newItem.currency
            }

            override fun areContentsTheSame(oldItem: ExchangeRate, newItem: ExchangeRate): Boolean {
                return oldItem == newItem
            }
        }
    }
}