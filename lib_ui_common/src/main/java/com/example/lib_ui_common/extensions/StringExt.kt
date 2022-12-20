package com.example.lib_ui_common.extensions

fun String.toDoubleOrZero(): Double {
    return try {
        this.toDoubleOrNull() ?: 0.0
    } catch (e: NumberFormatException) {
        0.0
    }
}