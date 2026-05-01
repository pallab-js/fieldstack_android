package com.fieldstack.android.ui.reports

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.domain.model.Report
import com.fieldstack.android.domain.model.ReportCategory
import com.fieldstack.android.domain.usecase.SaveReportUseCase
import com.fieldstack.android.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

data class ReportDraft(
    val id: String = UUID.randomUUID().toString(),
    val taskId: String = "",
    // Step 1
    val title: String = "",
    val category: ReportCategory = ReportCategory.Inspection,
    val priority: DraftPriority = DraftPriority.Medium,
    // Step 2
    val details: String = "",
    val notes: String = "",
    // Step 3
    val photoUris: List<String> = emptyList(),
    // Step 4
    val latitude: Double? = null,
    val longitude: Double? = null,
    // Step 5
    val signatureUri: String? = null,
    // Custom fields
    val customFields: List<com.fieldstack.android.domain.model.CustomField> = emptyList(),
)

enum class DraftPriority { Low, Medium, High }

sealed interface SaveState {
    data object Idle : SaveState
    data object Saving : SaveState
    data object Saved : SaveState
    data class Error(val message: String) : SaveState
}

@HiltViewModel
class ReportBuilderViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val saveReport: SaveReportUseCase,
    private val imageCompressor: ImageCompressor,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val taskId: String = savedState["taskId"] ?: ""

    private val _draft = MutableStateFlow(ReportDraft(taskId = taskId))
    val draft = _draft.asStateFlow()

    private val _step = MutableStateFlow(1)
    val step = _step.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState = _saveState.asStateFlow()

    // Step 1 fields
    fun setTitle(v: String)    = _draft.update { it.copy(title = v) }
    fun setCategory(v: ReportCategory) = _draft.update { it.copy(category = v) }
    fun setPriority(v: DraftPriority)  = _draft.update { it.copy(priority = v) }
    fun setAssetId(v: String)  = _draft.update { it.copy(title = if (_draft.value.title.isBlank()) v else _draft.value.title) }

    // Step 2 fields
    fun setDetails(v: String)  = _draft.update { it.copy(details = v) }
    fun setNotes(v: String)    = _draft.update { it.copy(notes = v) }

    // Step 3
    fun addPhoto(uri: String) = viewModelScope.launch {
        if (_draft.value.photoUris.size >= MAX_PHOTOS) return@launch
        val compressed = imageCompressor.compress(context, uri.toUri()).toString()
        _draft.update { it.copy(photoUris = it.photoUris + compressed) }
    }
    fun removePhoto(uri: String) = _draft.update { it.copy(photoUris = it.photoUris - uri) }

    // Step 4
    fun setLocation(lat: Double, lng: Double) = _draft.update { it.copy(latitude = lat, longitude = lng) }

    // Step 5
    fun setSignature(uri: String) = _draft.update { it.copy(signatureUri = uri) }

    // Custom fields
    fun addCustomField(field: com.fieldstack.android.domain.model.CustomField) =
        _draft.update { it.copy(customFields = it.customFields + field) }

    fun updateCustomField(id: String, value: String) =
        _draft.update {
            it.copy(customFields = it.customFields.map { f ->
                if (f.id == id) f.copy(value = value) else f
            })
        }

    fun removeCustomField(id: String) =
        _draft.update { it.copy(customFields = it.customFields.filter { f -> f.id != id }) }

    fun nextStep() { if (_step.value < 5) _step.update { it + 1 } }
    fun prevStep() { if (_step.value > 1) _step.update { it - 1 } }
    fun goToStep(s: Int) { _step.value = s.coerceIn(1, 6) }

    // Validation for current step
    fun isCurrentStepValid(): Boolean = when (_step.value) {
        1 -> _draft.value.title.isNotBlank()
        2 -> _draft.value.details.isNotBlank()
        else -> true
    }

    fun saveDraft() = viewModelScope.launch {
        _saveState.value = SaveState.Saving
        _saveState.value = try {
            saveReport(buildReport())
            SaveState.Saved
        } catch (e: Exception) {
            SaveState.Error(e.message ?: "Save failed")
        }
    }

    fun submit() = viewModelScope.launch {
        _saveState.value = SaveState.Saving
        _saveState.value = try {
            saveReport(buildReport())
            SaveState.Saved
        } catch (e: Exception) {
            SaveState.Error(e.message ?: "Submit failed")
        }
    }

    companion object {
        const val MAX_PHOTOS = 10
    }

    private fun buildReport() = Report(
        id = _draft.value.id,
        taskId = _draft.value.taskId,
        title = _draft.value.title,
        category = _draft.value.category,
        details = "${_draft.value.details}\n\n${_draft.value.notes}".trim(),
        photoUris = _draft.value.photoUris,
        latitude = _draft.value.latitude,
        longitude = _draft.value.longitude,
        signatureUri = _draft.value.signatureUri,
        customFields = _draft.value.customFields,
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
    )
}
