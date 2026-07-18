package com.wizpizz.reddidnt.preferences

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdBlockPreferencesTest {
    @Test
    fun featuresDefaultToEnabledAndUseIndependentKeys() {
        assertTrue(AdBlockPreferences.DEFAULT_ENABLED)
        assertNotEquals(
            AdBlockPreferences.PROMOTED_POSTS,
            AdBlockPreferences.COMMENT_ADS,
        )
    }
}
