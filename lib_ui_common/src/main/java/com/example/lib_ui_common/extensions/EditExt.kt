package com.example.lib_ui_common.extensions

import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged


fun AppCompatEditText.doAfterTextChangedIfNotEmpty(
    allowEmpty: Boolean = false,
    def: String = "",
    function: (String) -> Unit
) {
    this.doAfterTextChanged {
        if (it.toString().isNotEmpty()&&allowEmpty.not()) {
            function.invoke(it.toString())
        } else {
            this.setText(def)
        }
    }
}