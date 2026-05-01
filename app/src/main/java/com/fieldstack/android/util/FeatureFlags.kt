package com.fieldstack.android.util

import com.fieldstack.android.BuildConfig

/**
 * Compile-time feature flags backed by BuildConfig.
 *
 * To add a flag:
 *   1. Add a buildConfigField in the relevant buildType or productFlavor in build.gradle.kts.
 *   2. Expose it here as a val.
 *
 * All flags default to the safe/off state so that a missing BuildConfig field
 * never accidentally enables an unfinished feature in production.
 */
object FeatureFlags {
    /** Enable the delta-sync endpoint. Disabled until backend v2 is stable. */
    val deltaSyncEnabled: Boolean = BuildConfig.DEBUG

    /** Show the Insights dashboard tab. */
    val insightsDashboardEnabled: Boolean = true

    /** Enable PDF report export. */
    val pdfExportEnabled: Boolean = true

    /** Enable barcode/QR scanning in the report builder. */
    val barcodeScanEnabled: Boolean = true
}
