package com.dtire.dtireapp.utils

import android.view.View

interface StateCallback<T> {
    fun onSuccess(data: T)
    fun onLoading()
    fun onFailed(message: String?)

    val invisible: Int
        get() = View.INVISIBLE
    val visible: Int
        get() = View.VISIBLE
    val gone: Int
        get() = View.GONE
}