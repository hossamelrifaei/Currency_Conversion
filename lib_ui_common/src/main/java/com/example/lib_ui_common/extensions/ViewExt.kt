package com.example.lib_ui_common.extensions

import android.view.View

fun View.makeGone() {
    this.visibility = View.GONE
}

fun View.makeVisible() {
    this.visibility = View.VISIBLE
}