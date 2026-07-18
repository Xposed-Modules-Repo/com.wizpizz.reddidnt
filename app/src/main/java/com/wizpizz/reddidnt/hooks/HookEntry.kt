package com.wizpizz.reddidnt.hooks

import android.util.Log
import com.wizpizz.reddidnt.BuildConfig
import com.wizpizz.reddidnt.REDDIT_PACKAGE
import com.wizpizz.reddidnt.hooks.features.AdBlockFeature
import com.wizpizz.reddidnt.hooks.features.CommentAdBlocker
import com.wizpizz.reddidnt.hooks.features.PromotedPostBlocker
import com.wizpizz.reddidnt.preferences.AdBlockPreferences
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.matchers.MethodMatcher
import java.util.concurrent.atomic.AtomicBoolean

/** Modern libxposed entry point, loaded only in Reddit's scoped processes. */
class HookEntry : XposedModule() {
    private val initialized = AtomicBoolean(false)

    override fun onPackageReady(param: PackageReadyParam) {
        if (param.packageName != REDDIT_PACKAGE
            || !initialized.compareAndSet(false, true)) return

        val preferences = getRemotePreferences(AdBlockPreferences.GROUP)
        val features = listOf(PromotedPostBlocker, CommentAdBlocker)
        val classLoader = param.classLoader
        val apkPath = param.applicationInfo.sourceDir

        runCatching {
            System.loadLibrary("dexkit")
            val bridge = DexKitBridge.create(apkPath)
            bridge.use { bridge ->
                features.forEach { feature ->
                    installFeature(bridge, classLoader, feature) {
                        preferences.getBoolean(
                            feature.preferenceKey,
                            AdBlockPreferences.DEFAULT_ENABLED,
                        )
                    }
                }
            }
        }.onFailure { error ->
            log(Log.ERROR, TAG, "Ad-block initialization failed", error)
        }
    }

    private fun installFeature(
        bridge: DexKitBridge,
        classLoader: ClassLoader,
        feature: AdBlockFeature,
        isEnabled: () -> Boolean,
    ) {
        val methods = bridge.findMethod(
            FindMethod.create().matcher(
                MethodMatcher.create().usingStrings(feature.targetString),
            ),
        )

        val anchorHits = methods
            .distinctBy { it.methodSign }
            .filter { it.name != "<init>" }

        val candidates = anchorHits
            .filter { feature.matches(MethodShape.from(it)) }

        if (BuildConfig.DEBUG) {
            log(
                Log.DEBUG,
                TAG,
                "${feature.name}: ${anchorHits.size} anchor hit(s), " +
                    "${candidates.size} structural match(es)",
            )
            candidates.forEach { candidate ->
                log(Log.DEBUG, TAG, "${feature.name} candidate: ${candidate.descriptor}")
            }
        }

        val candidate = selectUniqueCandidate(candidates)
        if (candidate == null) {
            log(
                Log.ERROR,
                TAG,
                "${feature.name}: expected exactly 1 structural match, " +
                    "found ${candidates.size}; no hook installed",
            )
            return
        }

        runCatching {
            val method = candidate.getMethodInstance(classLoader)
            hook(method).intercept { chain ->
                if (isEnabled()) null else chain.proceed()
            }
        }.onSuccess {
            log(Log.INFO, TAG, "${feature.name}: installed 1 hook")
        }.onFailure { error ->
            log(Log.ERROR, TAG, "Failed to hook ${candidate.descriptor}", error)
        }
    }

    private companion object {
        const val TAG = "Reddidnt"
    }
}
