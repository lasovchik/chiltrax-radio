package com.chilltrax.radio.domain.validation

import java.net.URI

object StationInputValidator {

    fun isValidStreamUrl(value: String): Boolean {
        val trimmed = value.trim()
        if (trimmed.isBlank()) return false

        val uri = runCatching { URI(trimmed) }.getOrNull() ?: return false
        val scheme = uri.scheme?.lowercase()
        return (scheme == "http" || scheme == "https") && !uri.host.isNullOrBlank()
    }
}
