package com.chilltrax.radio.presentation.common

import android.content.Context
import androidx.annotation.StringRes

sealed interface UiMessage {

    data class Resource(
        @StringRes val resId: Int,
        val arg: String? = null,
    ) : UiMessage

    data class Text(val value: String) : UiMessage

    fun resolve(context: Context): String = when (this) {
        is Resource -> if (arg == null) {
            context.getString(resId)
        } else {
            context.getString(resId, arg)
        }
        is Text -> value
    }
}
