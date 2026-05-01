package com.fieldstack.android.domain.usecase

import com.fieldstack.android.data.local.ReportDao
import com.fieldstack.android.data.remote.FieldStackApi
import com.fieldstack.android.data.remote.toDomain
import com.fieldstack.android.data.remote.toDto
import javax.inject.Inject

/**
 * Fetches a report by ID from local storage and submits it to the remote API.
 * Returns true on success, false if the report is not found locally.
 */
class SyncReportUseCase @Inject constructor(
    private val reportDao: ReportDao,
    private val api: FieldStackApi,
) {
    suspend operator fun invoke(entityId: String): Boolean {
        val entity = reportDao.getById(entityId) ?: return false
        api.submitReport(entity.toDomain().toDto())
        return true
    }
}
