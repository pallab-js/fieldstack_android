plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.play.publisher)
    alias(libs.plugins.owasp.dependencycheck)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

// Lock all resolvable dependency configurations to prevent silent transitive upgrades.
// Regenerate lockfiles after any dependency change:
//   ./gradlew :app:dependencies --write-locks
dependencyLocking {
    lockAllConfigurations()
}

android {
    namespace = "com.fieldstack.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fieldstack.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0-mvp"

        testInstrumentationRunner = "com.fieldstack.android.HiltTestRunner"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }

        // Room schema export
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        create("staging") {
            initWith(getByName("release"))
            applicationIdSuffix = ".staging"
            isDebuggable = false
            isMinifyEnabled = true
            // Use a dedicated staging signing config if provided; fall back to release config.
            // Never use the debug keystore for staging — it allows sideloading alongside prod.
            signingConfig = signingConfigs.findByName("staging") ?: signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            buildConfigField("String", "API_BASE_URL", "\"https://dev-api.fieldstack.com/\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "API_BASE_URL", "\"https://api.fieldstack.com/\"")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

play {
    val credPath = System.getenv("PLAY_SERVICE_ACCOUNT_PATH")
        ?: findProperty("play.serviceAccountCredentials") as String?
    if (credPath != null) serviceAccountCredentials.set(file(credPath))
    track.set("internal")
    defaultToAppBundles.set(false)
}

dependencies {    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)

    // Compose
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.icons.extended)
    debugImplementation(libs.compose.ui.tooling)

    // Navigation
    implementation(libs.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // WorkManager
    implementation(libs.workmanager)

    // Network (wired in Phase 2; declared now to avoid config churn)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)

    // Auth / Media / Location
    implementation(libs.credentials)
    implementation(libs.biometric)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.location)

    // Preferences
    implementation(libs.datastore)
    implementation(libs.security.crypto)

    // Image loading
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)  // Coil 3 requires explicit network fetcher
    implementation(libs.mlkit.barcode)

    // Logging / Crash reporting
    implementation(libs.timber)
    val firebaseBom = platform(libs.firebase.bom)
    implementation(firebaseBom)
    implementation(libs.firebase.crashlytics)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.room.testing)
    testImplementation(libs.workmanager.testing)
    testImplementation(libs.mockwebserver)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
}
