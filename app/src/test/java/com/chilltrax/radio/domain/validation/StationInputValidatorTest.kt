package com.chilltrax.radio.domain.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StationInputValidatorTest {

    @Test
    fun acceptsHttpsStreamUrl() {
        assertTrue(StationInputValidator.isValidStreamUrl("https://streamssl.chilltrax.com/stream.mp3"))
    }

    @Test
    fun acceptsHttpUrlWithPort() {
        assertTrue(StationInputValidator.isValidStreamUrl("http://192.168.1.10:8000/stream"))
    }

    @Test
    fun rejectsBlankUrl() {
        assertFalse(StationInputValidator.isValidStreamUrl("   "))
    }

    @Test
    fun rejectsNonHttpScheme() {
        assertFalse(StationInputValidator.isValidStreamUrl("ftp://example.com/stream.mp3"))
    }

    @Test
    fun rejectsMalformedUrl() {
        assertFalse(StationInputValidator.isValidStreamUrl("not a url"))
    }
}
