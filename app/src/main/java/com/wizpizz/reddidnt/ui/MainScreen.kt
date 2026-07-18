package com.wizpizz.reddidnt.ui

import android.content.SharedPreferences
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.wizpizz.reddidnt.BuildConfig
import com.wizpizz.reddidnt.R
import com.wizpizz.reddidnt.REDDIT_PACKAGE
import com.wizpizz.reddidnt.RedditCompatibility
import com.wizpizz.reddidnt.RedditCompatibilityStatus
import com.wizpizz.reddidnt.TESTED_REDDIT_VERSION_NAME
import com.wizpizz.reddidnt.preferences.AdBlockPreferences
import com.wizpizz.reddidnt.readRedditCompatibility
import io.github.libxposed.service.XposedService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MainScreen(service: XposedService?, scopeRefreshEvent: Long) {
    val context = LocalContext.current
    val preferences = remember(service) {
        service?.getRemotePreferences(AdBlockPreferences.GROUP)
    }
    var promotedPosts by remember(preferences) {
        mutableStateOf(preferences.read(AdBlockPreferences.PROMOTED_POSTS))
    }
    var commentAds by remember(preferences) {
        mutableStateOf(preferences.read(AdBlockPreferences.COMMENT_ADS))
    }
    var moduleStatus by remember(service) {
        mutableStateOf(
            if (service == null) ModuleStatus.FRAMEWORK_UNAVAILABLE
            else ModuleStatus.CHECKING_SCOPE,
        )
    }
    var redditCompatibility by remember(context) {
        mutableStateOf(readRedditCompatibility(context.applicationContext))
    }

    LaunchedEffect(service, scopeRefreshEvent) {
        moduleStatus = if (service == null) {
            ModuleStatus.FRAMEWORK_UNAVAILABLE
        } else {
            withContext(Dispatchers.IO) {
                runCatching { service.scope }
                    .fold(
                        onSuccess = { scope ->
                            if (REDDIT_PACKAGE in scope) ModuleStatus.READY
                            else ModuleStatus.REDDIT_NOT_IN_SCOPE
                        },
                        onFailure = { ModuleStatus.SCOPE_CHECK_FAILED },
                    )
            }
        }
    }

    LaunchedEffect(scopeRefreshEvent) {
        redditCompatibility = withContext(Dispatchers.IO) {
            readRedditCompatibility(context.applicationContext)
        }
    }

    DisposableEffect(preferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            when (key) {
                AdBlockPreferences.PROMOTED_POSTS ->
                    promotedPosts = prefs.read(AdBlockPreferences.PROMOTED_POSTS)
                AdBlockPreferences.COMMENT_ADS ->
                    commentAds = prefs.read(AdBlockPreferences.COMMENT_ADS)
            }
        }
        preferences?.registerOnSharedPreferenceChangeListener(listener)
        onDispose { preferences?.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(Modifier.height(28.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_app_logo),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                            )
                        }
                    }
                    Column(Modifier.padding(start = 16.dp)) {
                        Text(
                            text = "Reddidn't",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Focused Reddit ad blocking",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
                RedditCompatibilityCard(compatibility = redditCompatibility)
                Spacer(Modifier.height(12.dp))
                StatusCard(status = moduleStatus)
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Ad blocking",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(10.dp))
                SettingRow(
                    title = "Promoted posts",
                    description = "Hide promoted posts in feeds",
                    checked = promotedPosts,
                    enabled = preferences != null,
                    onCheckedChange = { enabled ->
                        promotedPosts = enabled
                        preferences?.edit {
                            putBoolean(AdBlockPreferences.PROMOTED_POSTS, enabled)
                        }
                    },
                )
                Spacer(Modifier.height(10.dp))
                SettingRow(
                    title = "Comment ads",
                    description = "Hide ads in comment threads",
                    checked = commentAds,
                    enabled = preferences != null,
                    onCheckedChange = { enabled ->
                        commentAds = enabled
                        preferences?.edit {
                            putBoolean(AdBlockPreferences.COMMENT_ADS, enabled)
                        }
                    },
                )
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun RedditCompatibilityCard(compatibility: RedditCompatibility) {
    val title = "Compatibility"
    val baseDescription = "Reddidn't ${BuildConfig.VERSION_NAME} was developed for " +
        "Reddit $TESTED_REDDIT_VERSION_NAME(+)."
    val linkColor = MaterialTheme.colorScheme.primary
    val description = buildAnnotatedString {
        append(baseDescription)
        when (compatibility.status) {
            RedditCompatibilityStatus.SAME_OR_OLDER ->
                append(" Installed: ${compatibility.installedVersionName}.")
            RedditCompatibilityStatus.NEWER -> {
                append(" Installed: ${compatibility.installedVersionName}. ")
                append("If ad blocking is not working, ")
                withLink(
                    LinkAnnotation.Url(
                        url = RELEASES_URL,
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline,
                            ),
                        ),
                    ),
                ) {
                    append("check for a newer Reddidn't release")
                }
                append(" or wait for an update.")
            }
            RedditCompatibilityStatus.NOT_INSTALLED -> append(" Reddit is not installed.")
            RedditCompatibilityStatus.UNAVAILABLE ->
                append(" The installed Reddit version could not be read.")
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private const val RELEASES_URL = "https://github.com/oenderg/Reddidnt/releases"

@Composable
private fun StatusCard(status: ModuleStatus) {
    val (container, content) = when (status) {
        ModuleStatus.READY ->
            MaterialTheme.colorScheme.primaryContainer to
                MaterialTheme.colorScheme.onPrimaryContainer
        ModuleStatus.REDDIT_NOT_IN_SCOPE ->
            MaterialTheme.colorScheme.tertiaryContainer to
                MaterialTheme.colorScheme.onTertiaryContainer
        else ->
            MaterialTheme.colorScheme.surfaceContainerHigh to
                MaterialTheme.colorScheme.onSurface
    }
    val (title, description) = when (status) {
        ModuleStatus.FRAMEWORK_UNAVAILABLE ->
            "Framework unavailable" to "Open LSPosed and make sure the framework is running"
        ModuleStatus.CHECKING_SCOPE ->
            "Checking module scope" to "Reading the current LSPosed configuration"
        ModuleStatus.REDDIT_NOT_IN_SCOPE ->
            "Reddit not in scope" to "Select Reddit in the module's LSPosed scope"
        ModuleStatus.SCOPE_CHECK_FAILED ->
            "Scope check failed" to "Reopen the app or check LSPosed"
        ModuleStatus.READY ->
            "Ready" to "If you still see ads, make sure the module is enabled, then force-stop and reopen Reddit"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = container),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                text = title,
                color = content,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                color = content,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private enum class ModuleStatus {
    FRAMEWORK_UNAVAILABLE,
    CHECKING_SCOPE,
    REDDIT_NOT_IN_SCOPE,
    SCOPE_CHECK_FAILED,
    READY,
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f).padding(end = 16.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Switch(
                checked = checked,
                enabled = enabled,
                onCheckedChange = null,
            )
        }
    }
}

private fun SharedPreferences?.read(key: String): Boolean =
    this?.getBoolean(key, AdBlockPreferences.DEFAULT_ENABLED)
        ?: AdBlockPreferences.DEFAULT_ENABLED
