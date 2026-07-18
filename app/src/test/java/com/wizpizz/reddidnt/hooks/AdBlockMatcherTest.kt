package com.wizpizz.reddidnt.hooks

import com.wizpizz.reddidnt.hooks.features.CommentAdBlocker
import com.wizpizz.reddidnt.hooks.features.PromotedPostBlocker
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class AdBlockMatcherTest {
    private val promoted2026_28 = MethodShape(
        className = "com.reddit.ads.impl.feeds.composables.c",
        methodName = "d",
        returnTypeName = "void",
        parameterTypeNames = listOf(
            "defpackage.dfv",
            "kotlin.jvm.functions.Function0",
            "defpackage.h010",
            "defpackage.yv10",
            "defpackage.cfh",
            "defpackage.j8w",
            "defpackage.dfv",
            "defpackage.sab",
            "int",
        ),
        usingStrings = listOf("promoted_post_unit"),
    )

    private val comment2026_28 = MethodShape(
        className = "defpackage.ar9",
        methodName = "b",
        returnTypeName = "void",
        parameterTypeNames = listOf(
            "defpackage.wk9",
            "boolean",
            "boolean",
            "float",
            "kotlin.jvm.functions.Function1",
            "defpackage.p1b",
            "defpackage.p1b",
            "boolean",
            "defpackage.dfv",
            "com.reddit.ads.features.ClickVisibilityDurationVariant",
            "defpackage.ob8",
            "kotlin.jvm.functions.Function0",
            "kotlin.jvm.functions.Function0",
            "kotlin.jvm.functions.Function1",
            "kotlin.jvm.functions.Function0",
            "defpackage.sab",
            "int",
            "int",
            "int",
        ),
        usingStrings = listOf("blank_ad_container"),
    )

    private val commentLeafRenderer2026_28 = MethodShape(
        className = "defpackage.dk9",
        methodName = "a",
        returnTypeName = "void",
        parameterTypeNames = listOf(
            "defpackage.uk9",
            "kotlin.jvm.functions.Function1",
            "boolean",
            "defpackage.p1b",
            "defpackage.p1b",
            "defpackage.dfv",
            "kotlin.jvm.functions.Function0",
            "kotlin.jvm.functions.Function0",
            "kotlin.jvm.functions.Function1",
            "defpackage.sab",
            "int",
        ),
        usingStrings = listOf("conversation_screen_ad"),
    )

    @Test
    fun promotedMatcherAcceptsObserved2026_28Shape() {
        assertTrue(PromotedPostBlocker.matches(promoted2026_28))
    }

    @Test
    fun promotedMatcherAcceptsMinorShapeDrift() {
        val drifted = promoted2026_28.copy(
            methodName = "renderPromotedPost",
            parameterTypeNames = listOf(
                "obfuscated.Model",
                "kotlin.jvm.functions.Function2",
                "boolean",
                "obfuscated.Layout",
                "obfuscated.Composer",
                "int",
            ),
        )

        assertTrue(PromotedPostBlocker.matches(drifted))
    }

    @Test
    fun promotedMatcherRejectsMissingTargetString() {
        assertFalse(PromotedPostBlocker.matches(promoted2026_28.copy(usingStrings = emptyList())))
    }

    @Test
    fun promotedMatcherRejectsNonVoidReturn() {
        assertFalse(
            PromotedPostBlocker.matches(promoted2026_28.copy(returnTypeName = "java.lang.Object")),
        )
    }

    @Test
    fun promotedMatcherRejectsOutOfRangeParameterCount() {
        assertFalse(
            PromotedPostBlocker.matches(
                promoted2026_28.copy(parameterTypeNames = List(5) { "int" }),
            ),
        )
        assertFalse(
            PromotedPostBlocker.matches(
                promoted2026_28.copy(parameterTypeNames = List(15) { "int" }),
            ),
        )
    }

    @Test
    fun promotedMatcherRejectsMissingTrailingInt() {
        val parameters = promoted2026_28.parameterTypeNames.dropLast(1) + "defpackage.sab"
        assertFalse(
            PromotedPostBlocker.matches(promoted2026_28.copy(parameterTypeNames = parameters)),
        )
    }

    @Test
    fun promotedMatcherRejectsCandidateOutsideAdsFeedPackage() {
        assertFalse(
            PromotedPostBlocker.matches(
                promoted2026_28.copy(className = "defpackage.nr0"),
            ),
        )
    }

    @Test
    fun promotedMatcherRejectsObservedSharedSyntheticDispatcher() {
        val sharedDispatcher = MethodShape(
            className = "defpackage.nr0",
            methodName = "invoke",
            returnTypeName = "java.lang.Object",
            parameterTypeNames = List(4) { "java.lang.Object" },
            usingStrings = listOf("promoted_post_unit"),
        )

        assertFalse(PromotedPostBlocker.matches(sharedDispatcher))
    }

    @Test
    fun commentMatcherAcceptsObserved2026_28Shape() {
        assertTrue(CommentAdBlocker.matches(comment2026_28))
    }

    @Test
    fun commentMatcherAcceptsMinorShapeDrift() {
        val drifted = comment2026_28.copy(
            methodName = "renderCommentAdvertisementSlot",
            parameterTypeNames = listOf(
                "obfuscated.AdSlotState",
                "boolean",
                "kotlin.jvm.functions.Function2",
                "obfuscated.CallbackState",
                "kotlin.jvm.functions.Function0",
                "obfuscated.Modifier",
                "boolean",
                "obfuscated.VisibilityState",
                "kotlin.jvm.functions.Function3",
                "obfuscated.Analytics",
                "obfuscated.Layout",
                "boolean",
                "obfuscated.Composer",
                "int",
                "int",
            ),
        )

        assertTrue(CommentAdBlocker.matches(drifted))
    }

    @Test
    fun commentMatcherRejectsMissingTargetString() {
        assertFalse(CommentAdBlocker.matches(comment2026_28.copy(usingStrings = emptyList())))
    }

    @Test
    fun commentMatcherRejectsNonVoidReturn() {
        assertFalse(CommentAdBlocker.matches(comment2026_28.copy(returnTypeName = "boolean")))
    }

    @Test
    fun commentMatcherRejectsOutOfRangeParameterCount() {
        assertFalse(
            CommentAdBlocker.matches(
                comment2026_28.copy(parameterTypeNames = List(14) { "int" }),
            ),
        )
        assertFalse(
            CommentAdBlocker.matches(
                comment2026_28.copy(parameterTypeNames = List(25) { "int" }),
            ),
        )
    }

    @Test
    fun commentMatcherRejectsFewerThanTwoTrailingInts() {
        val parameters = comment2026_28.parameterTypeNames.toMutableList().apply {
            this[lastIndex - 1] = "defpackage.sab"
        }
        assertFalse(CommentAdBlocker.matches(comment2026_28.copy(parameterTypeNames = parameters)))
    }

    @Test
    fun commentMatcherRejectsFewerThanTwoKotlinFunctionParameters() {
        var retainedFunction = false
        val parameters = comment2026_28.parameterTypeNames.map {
            if (!it.startsWith("kotlin.jvm.functions.Function")) {
                it
            } else if (!retainedFunction) {
                retainedFunction = true
                it
            } else {
                "obfuscated.Callback"
            }
        }
        assertFalse(CommentAdBlocker.matches(comment2026_28.copy(parameterTypeNames = parameters)))
    }

    @Test
    fun commentMatcherRejectsLeafRendererThatCannotPreventPlaceholder() {
        assertFalse(CommentAdBlocker.matches(commentLeafRenderer2026_28))
    }

    @Test
    fun uniqueSelectionAcceptsExactlyOneCandidate() {
        assertSame(promoted2026_28, selectUniqueCandidate(listOf(promoted2026_28)))
    }

    @Test
    fun uniqueSelectionRejectsZeroCandidates() {
        assertNull(selectUniqueCandidate<MethodShape>(emptyList()))
    }

    @Test
    fun uniqueSelectionRejectsMultipleCandidates() {
        assertNull(
            selectUniqueCandidate(
                listOf(promoted2026_28, promoted2026_28.copy(methodName = "e")),
            ),
        )
    }
}
