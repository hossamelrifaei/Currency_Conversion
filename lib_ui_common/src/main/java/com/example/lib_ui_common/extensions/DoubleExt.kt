package com.example.lib_ui_common.extensions

fun Double.roundDoubleToString(decimalPlaces: Int = 2): String = "%.${decimalPlaces}f".format(this)