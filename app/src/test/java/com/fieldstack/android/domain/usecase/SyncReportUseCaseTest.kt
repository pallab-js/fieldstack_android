package com.fieldstack.android.domain.usecase

import com.fieldstack.android.data.local.ReportDao
import com.fieldstack.android.data.local.ReportEntity
import com.fieldstack.android.data.remote.FieldStackApi
import com.fieldstack.android.data.remote.SubmitReportResponse
import com.fieldstack.android.domain.model.ReportCategory
import com.fieldstack.android.domain.model.SyncStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class SyncReportUseCaseTest {

    private val reportDao = mockk<ReportDao>()
    private val api = mockk<FieldStackApi>()
    private val useCase = SyncReportUseCase(reportDao, api)

    private val entity = ReportEntity(
        id = "r1", taskId = "t1", title = "Test", category = ReportCategory.Inspection,
        details = "Details", photoUris = emptyList(), latitude = null, longitude = null,
        signatureUri = null, createdAt = Instant.now(), updatedAt = Instant.now(),
        syncStatus = SyncStatus.Pending,
    )

    @Test
    fun `returns false when report not found`() = runTest {
        coEvery { reportDao.getById("missing") } returns null
        assertFalse(useCase("missing"))
    }

    @Test
    fun `submits report and returns true on success`() = runTest {
        coEvery { reportDao.getById("r1") } returns entity
        coEvery { api.submitReport(any()) } returns SubmitReportResponse("server-r1")
        assertTrue(useCase("r1"))
        coVerify { api.submitReport(any()) }
    }

    @Test
    fun `propagates exception from API`() = runTest {
        coEvery { reportDao.getById("r1") } returns entity
        coEvery { api.submitReport(any()) } throws RuntimeException("Server error")
        try {
            useCase("r1")
            assert(false) { "Expected exception" }
        } catch (e: RuntimeException) {
            assertTrue(e.message == "Server error")
        }
    }
}
