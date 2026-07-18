package com.wizpizz.reddidnt

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

internal enum class RedditCompatibilityStatus {
    SAME_OR_OLDER,
    NEWER,
    NOT_INSTALLED,
    UNAVAILABLE,
}

internal data class RedditCompatibility(
    val status: RedditCompatibilityStatus,
    val installedVersionName: String? = null,
)

internal sealed interface RedditPackageLookup {
    data class Found(
        val versionName: String?,
        val versionCode: Long,
    ) : RedditPackageLookup

    data object Missing : RedditPackageLookup

    data object Unavailable : RedditPackageLookup
}

internal fun classifyRedditCompatibility(
    lookup: RedditPackageLookup,
): RedditCompatibility = when (lookup) {
    is RedditPackageLookup.Found -> RedditCompatibility(
        status = if (lookup.versionCode > TESTED_REDDIT_VERSION_CODE) {
            RedditCompatibilityStatus.NEWER
        } else {
            RedditCompatibilityStatus.SAME_OR_OLDER
        },
        installedVersionName = lookup.versionName
            ?.takeIf(String::isNotBlank)
            ?: lookup.versionCode.toString(),
    )
    RedditPackageLookup.Missing -> RedditCompatibility(RedditCompatibilityStatus.NOT_INSTALLED)
    RedditPackageLookup.Unavailable -> RedditCompatibility(RedditCompatibilityStatus.UNAVAILABLE)
}

internal fun readRedditCompatibility(context: Context): RedditCompatibility {
    val lookup = try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                REDDIT_PACKAGE,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(REDDIT_PACKAGE, 0)
        }
        RedditPackageLookup.Found(
            versionName = packageInfo.versionName,
            versionCode = packageInfo.longVersionCode,
        )
    } catch (_: PackageManager.NameNotFoundException) {
        RedditPackageLookup.Missing
    } catch (_: Exception) {
        RedditPackageLookup.Unavailable
    }

    return classifyRedditCompatibility(lookup)
}
