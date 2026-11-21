plugins {
    alias(libs.plugins.android.application)
}

val signingEnvironment = mapOf(
    "KEYSTORE_FILE" to providers.environmentVariable("KEYSTORE_FILE").orNull,
    "KEYSTORE_PASSWORD" to providers.environmentVariable("KEYSTORE_PASSWORD").orNull,
    "KEY_ALIAS" to providers.environmentVariable("KEY_ALIAS").orNull,
    "KEY_PASSWORD" to providers.environmentVariable("KEY_PASSWORD").orNull,
)
val configuredSigningValues = signingEnvironment.values.count { !it.isNullOrBlank() }
if (configuredSigningValues != 0 && configuredSigningValues != signingEnvironment.size) {
    throw GradleException("All release signing environment variables must be provided together.")
}
val releaseSigningConfigured = configuredSigningValues == signingEnvironment.size

android {
    namespace = "com.anmoltanwar.tatasky"
    compileSdk {
        version = release(37) {
            minorApiLevel = 2
        }
    }
    defaultConfig {
        applicationId = "com.anmoltanwar.tatasky"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }
    signingConfigs {
        if (releaseSigningConfigured) {
            create("release") {
                storeFile = file(signingEnvironment.getValue("KEYSTORE_FILE")!!)
                storePassword = signingEnvironment.getValue("KEYSTORE_PASSWORD")
                keyAlias = signingEnvironment.getValue("KEY_ALIAS")
                keyPassword = signingEnvironment.getValue("KEY_PASSWORD")
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
            if (releaseSigningConfigured) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
