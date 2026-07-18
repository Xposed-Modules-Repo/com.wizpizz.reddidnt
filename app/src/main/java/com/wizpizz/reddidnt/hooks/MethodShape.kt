package com.wizpizz.reddidnt.hooks

import org.luckypray.dexkit.result.MethodData

/** Stable method characteristics used by matchers instead of obfuscated identities. */
data class MethodShape(
    val className: String,
    val methodName: String,
    val returnTypeName: String,
    val parameterTypeNames: List<String>,
    val usingStrings: List<String>,
) {
    val parameterCount: Int get() = parameterTypeNames.size

    companion object {
        fun from(method: MethodData): MethodShape = MethodShape(
            className = method.className,
            methodName = method.name,
            returnTypeName = method.returnTypeName,
            parameterTypeNames = method.paramTypeNames,
            usingStrings = method.usingStrings,
        )
    }
}
