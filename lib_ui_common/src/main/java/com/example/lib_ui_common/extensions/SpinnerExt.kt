package com.example.lib_ui_common.extensions

import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner

fun AppCompatSpinner.onItemSelected(function: (Int) -> Unit) {
    val listener = this.onItemSelectedListener
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            function(position)
            listener?.onItemSelected(parent, view, position, id)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            listener?.onNothingSelected(parent)
        }
    }
}