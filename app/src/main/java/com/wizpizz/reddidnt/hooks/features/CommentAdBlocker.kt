package com.wizpizz.reddidnt.hooks.features

import com.wizpizz.reddidnt.hooks.MethodShape
import com.wizpizz.reddidnt.preferences.AdBlockPreferences

object CommentAdBlocker : AdBlockFeature {
    override val name = "Comment ads"
    override val targetString = "blank_ad_container"
    override val preferenceKey = AdBlockPreferences.COMMENT_ADS

    override fun matches(method: MethodShape): Boolean {
        val parameters = method.parameterTypeNames
        val kotlinFunctionCount = parameters.count {
            it.startsWith("kotlin.jvm.functions.Function")
        }
        val trailingIntCount = parameters.asReversed().takeWhile { it == "int" }.size

        return targetString in method.usingStrings &&
            method.returnTypeName == "void" &&
            method.parameterCount in 15..24 &&
            kotlinFunctionCount >= 2 &&
            trailingIntCount >= 2
    }
}
