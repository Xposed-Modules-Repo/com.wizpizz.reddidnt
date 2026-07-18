package com.wizpizz.reddidnt.hooks.features

import com.wizpizz.reddidnt.hooks.MethodShape

/** Description of one independently configurable ad renderer hook. */
interface AdBlockFeature {
    val name: String
    val targetString: String
    val preferenceKey: String

    fun matches(method: MethodShape): Boolean
}
