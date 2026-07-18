package com.wizpizz.reddidnt.hooks

/** Returns the candidate only when discovery produced one unambiguous target. */
internal fun <T> selectUniqueCandidate(candidates: List<T>): T? =
    candidates.singleOrNull()
