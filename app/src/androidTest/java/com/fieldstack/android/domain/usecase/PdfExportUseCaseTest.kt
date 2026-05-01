package com.fieldstack.android.domain.usecase

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fieldstack.android.domain.model.Report
import com.fieldstack.android.domain.model.ReportCategory
import com.fieldstack.android.domain.model.SyncStatus
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class PdfExportUseCaseTest {

    private val useCase = PdfExportUseCase(ApplicationProvider.getApplicationContext())

    @Test
    fun `export creates a non-empty PDF file`() {
        val report = Report(
            id = "r-pdf", taskId = "t1", title = "Site A Inspection",
            category = ReportCategory.Inspection,
            details = "All systems nominal. No issues found during walkthrough.",
            photoUris = listOf("file://photo1.jpg"),
            latitude = 40.7128, longitude = -74.0060,
            signatureUri = "/data/sig.png",
            createdAt = Instant.now(), updatedAt = Instant.now(),
            syncStatus = SyncStatus.Synced,
        )
        val file = useCase(report)
        assertTrue("PDF file should exist", file.exists())
        assertTrue("PDF file should not be empty", file.length() > 0)
        // Verify PDF magic bytes
        val header = file.readBytes().take(4)
        assertTrue("%PDF header", header[0] == '%'.code.toByte() && header[1] == 'P'.code.toByte())
    }
}
