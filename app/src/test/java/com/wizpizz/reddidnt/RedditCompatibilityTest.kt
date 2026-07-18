package com.wizpizz.reddidnt

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RedditCompatibilityTest {
    @Test
    fun testedVersionIsInformational() {
        val result = classifyRedditCompatibility(
            RedditPackageLookup.Found("2026.28.0", TESTED_REDDIT_VERSION_CODE),
        )

        assertEquals(RedditCompatibilityStatus.SAME_OR_OLDER, result.status)
        assertEquals("2026.28.0", result.installedVersionName)
    }

    @Test
    fun olderVersionIsInformational() {
        val result = classifyRedditCompatibility(
            RedditPackageLookup.Found("2026.27.1", TESTED_REDDIT_VERSION_CODE - 1),
        )

        assertEquals(RedditCompatibilityStatus.SAME_OR_OLDER, result.status)
    }

    @Test
    fun newerVersionUsesWarningWording() {
        val result = classifyRedditCompatibility(
            RedditPackageLookup.Found("2026.29.0", TESTED_REDDIT_VERSION_CODE + 1),
        )

        assertEquals(RedditCompatibilityStatus.NEWER, result.status)
    }

    @Test
    fun missingPackageMapsToNotInstalled() {
        val result = classifyRedditCompatibility(RedditPackageLookup.Missing)

        assertEquals(RedditCompatibilityStatus.NOT_INSTALLED, result.status)
        assertNull(result.installedVersionName)
    }

    @Test
    fun unreadablePackageMapsToUnavailable() {
        val result = classifyRedditCompatibility(RedditPackageLookup.Unavailable)

        assertEquals(RedditCompatibilityStatus.UNAVAILABLE, result.status)
        assertNull(result.installedVersionName)
    }
}
