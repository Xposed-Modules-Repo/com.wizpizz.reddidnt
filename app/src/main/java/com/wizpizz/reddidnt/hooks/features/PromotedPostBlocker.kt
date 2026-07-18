package com.wizpizz.reddidnt.hooks.features

import com.wizpizz.reddidnt.hooks.MethodShape
import com.wizpizz.reddidnt.preferences.AdBlockPreferences

object PromotedPostBlocker : AdBlockFeature {
    override val name = "Promoted posts"
    override val targetString = "promoted_post_unit"
    override val preferenceKey = AdBlockPreferences.PROMOTED_POSTS

    override fun matches(method: MethodShape): Boolean {
        val parameters = method.parameterTypeNames

        return targetString in method.usingStrings &&
            method.className.startsWith(FEED_COMPOSABLE_PACKAGE) &&
            method.returnTypeName == "void" &&
            method.parameterCount in 6..14 &&
            parameters.lastOrNull() == "int"
    }

    private const val FEED_COMPOSABLE_PACKAGE =
        "com.reddit.ads.impl.feeds.composables."
}
