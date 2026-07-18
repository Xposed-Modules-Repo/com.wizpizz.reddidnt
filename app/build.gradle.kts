import java.util.Base64

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.wizpizz.reddidnt"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.wizpizz.reddidnt"
        minSdk = 29
        versionCode = 8
        versionName = "2.0.0"

    }

    signingConfigs {
        // Read from environment first; if missing, read from a local .env file (ignored by Git)
        val env = System.getenv()
        val dotEnvFile = rootProject.file(".env")
        val dotEnv: Map<String, String> = if (dotEnvFile.exists()) {
            dotEnvFile.readLines()
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains('=') }
                .associate { line ->
                    val idx = line.indexOf('=')
                    val key = line.substring(0, idx).trim()
                    var value = line.substring(idx + 1).trim()
                    if ((value.startsWith('"') && value.endsWith('"')) || (value.startsWith('\'') && value.endsWith('\''))) {
                        value = value.substring(1, value.length - 1)
                    }
                    key to value
                }
        } else emptyMap()

        fun readSecret(name: String): String? = env[name] ?: dotEnv[name]

        val ksPath = readSecret("SIGNING_KEY_STORE_PATH")
        // keystore path as b64 in .env instead of raw file (if lazy)
        var ksPath64 = readSecret("SIGNING_KEY_STORE_BASE64")
        if (ksPath64?.isNotEmpty() == true) {
            val decodedBytes = Base64.getDecoder().decode(ksPath64)
            val tempFile = File.createTempFile("keystore", "")
            tempFile.writeBytes(decodedBytes)
            tempFile.deleteOnExit()
            ksPath64 = tempFile.absolutePath
        }
        val ksAlias = readSecret("SIGNING_KEY_ALIAS")
        val ksStorePassword = readSecret("SIGNING_KEY_STORE_PASSWORD")
        val ksKeyPassword = readSecret("SIGNING_KEY_PASSWORD")

        create("release"){
            if ((!ksPath64.isNullOrEmpty() || !ksPath.isNullOrEmpty()) && !ksAlias.isNullOrEmpty() && !ksStorePassword.isNullOrEmpty() && !ksKeyPassword.isNullOrEmpty()) {
                storeFile = let {
                    if (!ksPath64.isNullOrEmpty()) {
                        file(ksPath64)
                    } else
                    (ksPath as Any?)?.let { file(it) }
                }
                storePassword = ksStorePassword
                keyAlias = ksAlias
                keyPassword = ksKeyPassword
            } else {
                println(
                    """

                          _    _  _____ _____ _   _  _____      _____  ______ ____  _    _  _____      _  __________     __
                         | |  | |/ ____|_   _| \ | |/ ____|    |  __ \|  ____|  _ \| |  | |/ ____|    | |/ /  ____\ \   / /
                         | |  | | (___   | | |  \| | |  __     | |  | | |__  | |_) | |  | | |  __     | ' /| |__   \ \_/ /
                         | |  | |\___ \  | | | . ` | | |_ |    | |  | |  __| |  _ <| |  | | | |_ |    |  < |  __|   \   /
                         | |__| |____) |_| |_| |\  | |__| |    | |__| | |____| |_) | |__| | |__| |    | . \| |____   | |
                          \____/|_____/|_____|_| \_|\_____|    |_____/|______|____/ \____/ \_____|    |_|\_\______|  |_|



                    """.trimIndent()
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // CI signs releases when credentials are present. Local verification
            // still produces an unsigned release APK without private keys.
            signingConfig = signingConfigs.findByName("release")?.takeIf {
                it.storeFile != null
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    // Modern libxposed API is supplied by the framework at runtime.
    compileOnly(libs.libxposed.api)
    implementation(libs.libxposed.service)
    implementation(libs.dexkit)
}
