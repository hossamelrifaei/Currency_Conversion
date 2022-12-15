package com.example.lib_ui_common.state

sealed class ViewState<out T> {
    object Loading : ViewState<Nothing>()
    object Error : ViewState<Nothing>()
    data class Success<T>(val data: T) : ViewState<T>()
}
