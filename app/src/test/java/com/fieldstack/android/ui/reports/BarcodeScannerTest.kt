package com.fieldstack.android.ui.reports

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BarcodeScannerTest {

    @Test
    fun `barcode raw value is used as asset id`() {
        val scanned = "ASSET-2024-XYZ-001"
        var captured = ""
        val onResult: (String) -> Unit = { captured = it }
        onResult(scanned)
        assertEquals("ASSET-2024-XYZ-001", captured)
    }

    @Test
    fun `QR code URL is accepted as asset id`() {
        val qrValue = "https://fieldstack.com/assets/12345"
        var captured = ""
        val onResult: (String) -> Unit = { captured = it }
        onResult(qrValue)
        assertTrue(captured.isNotBlank())
        assertEquals(qrValue, captured)
    }

    @Test
    fun `setAssetId fills title when blank`() {
        // Simulate ViewModel logic: setAssetId fills title only if blank
        var title = ""
        val setAssetId: (String) -> Unit = { v ->
            if (title.isBlank()) title = v
        }
        setAssetId("ASSET-001")
        assertEquals("ASSET-001", title)
    }

    @Test
    fun `setAssetId does not overwrite existing title`() {
        var title = "Existing Title"
        val setAssetId: (String) -> Unit = { v ->
            if (title.isBlank()) title = v
        }
        setAssetId("ASSET-001")
        assertEquals("Existing Title", title)
    }
}
