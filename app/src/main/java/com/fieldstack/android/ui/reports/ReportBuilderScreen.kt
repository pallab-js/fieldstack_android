package com.fieldstack.android.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldstack.android.domain.model.ReportCategory
import com.fieldstack.android.ui.components.ZenButton
import com.fieldstack.android.ui.components.ZenButtonVariant
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.components.ZenFilterChip
import com.fieldstack.android.ui.components.ZenProgressBar
import com.fieldstack.android.ui.components.ZenTextField
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone

@Composable
fun ReportBuilderScreen(
    taskId: String,
    viewModel: ReportBuilderViewModel = hiltViewModel(),
) {
    val draft     by viewModel.draft.collectAsStateWithLifecycle()
    val step      by viewModel.step.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    // Step 6 = review screen (logical, not stored in VM)
    val showReview = step > 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.sm),
    ) {
        if (!showReview) {
            Text("Step $step of 5", style = MaterialTheme.typography.labelMedium, color = Stone)
            Spacer(Modifier.height(6.dp))
            ZenProgressBar(progress = step / 5f, label = "Step $step of 5", modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(Spacing.sm))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            when {
                showReview -> ReviewScreen(
                    draft = draft,
                    isOnline = true, // wired to real connectivity in Task 14
                    isSaving = saveState is SaveState.Saving,
                    onSubmit = { viewModel.submit() },
                )
                step == 1 -> Step1BasicInfo(
                    draft = draft,
                    onTitleChange = viewModel::setTitle,
                    onCategoryChange = viewModel::setCategory,
                    onPriorityChange = viewModel::setPriority,
                    onAssetScanned = viewModel::setAssetId,
                )
                step == 2 -> Step2Details(
                    draft = draft,
                    onDetailsChange = viewModel::setDetails,
                    onNotesChange = viewModel::setNotes,
                    onAddField = viewModel::addCustomField,
                    onUpdateField = viewModel::updateCustomField,
                    onRemoveField = viewModel::removeCustomField,
                )
                step == 3 -> Step3Media(
                    draft = draft,
                    onPhotoAdded = viewModel::addPhoto,
                    onPhotoRemoved = viewModel::removePhoto,
                )
                step == 4 -> Step4Location(
                    draft = draft,
                    onLocationSet = viewModel::setLocation,
                )
                step == 5 -> Step5Signature(
                    draft = draft,
                    onSignatureSaved = viewModel::setSignature,
                )
            }
        }

        if (!showReview) {
            Spacer(Modifier.height(Spacing.xs))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (step > 1) {
                    ZenButton(
                        text = "← Back",
                        onClick = viewModel::prevStep,
                        variant = ZenButtonVariant.Ghost,
                        modifier = Modifier.weight(1f),
                    )
                }
                ZenButton(
                    text = "Save Draft",
                    onClick = viewModel::saveDraft,
                    variant = ZenButtonVariant.Secondary,
                    modifier = Modifier.weight(1f),
                )
                ZenButton(
                    text = if (step == 5) "Review →" else "Next →",
                    onClick = {
                        if (viewModel.isCurrentStepValid()) {
                            if (step == 5) viewModel.goToStep(6)
                            else viewModel.nextStep()
                        }
                    },
                    enabled = viewModel.isCurrentStepValid(),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun Step1BasicInfo(
    draft: ReportDraft,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (ReportCategory) -> Unit,
    onPriorityChange: (DraftPriority) -> Unit,
    onAssetScanned: (String) -> Unit = {},
) {
    var showScanner by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text("Basic Info", style = MaterialTheme.typography.headlineMedium)
        ZenTextField(
            value = draft.title,
            onValueChange = onTitleChange,
            label = "Report Title",
            placeholder = "e.g. Site A Inspection",
            contentDesc = "Report title",
            modifier = Modifier.fillMaxWidth(),
        )
        if (showScanner) {
            BarcodeScanner(
                onResult = { value ->
                    onAssetScanned(value)
                    showScanner = false
                },
                onDismiss = { showScanner = false },
            )
        } else {
            ZenButton(
                text = "Scan Asset",
                onClick = { showScanner = true },
                variant = ZenButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Text("Category", style = MaterialTheme.typography.bodySmall, color = Stone)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReportCategory.entries.forEach { cat ->
                ZenFilterChip(label = cat.name, selected = draft.category == cat,
                    onClick = { onCategoryChange(cat) })
            }
        }
        Text("Priority", style = MaterialTheme.typography.bodySmall, color = Stone)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DraftPriority.entries.forEach { p ->
                ZenFilterChip(label = p.name, selected = draft.priority == p,
                    onClick = { onPriorityChange(p) })
            }
        }
    }
}

@Composable
private fun Step2Details(
    draft: ReportDraft,
    onDetailsChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onAddField: (com.fieldstack.android.domain.model.CustomField) -> Unit = {},
    onUpdateField: (String, String) -> Unit = { _, _ -> },
    onRemoveField: (String) -> Unit = {},
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text("Details", style = MaterialTheme.typography.headlineMedium)
        ZenTextField(
            value = draft.details,
            onValueChange = onDetailsChange,
            label = "Findings / Observations",
            placeholder = "Describe what you found…",
            errorText = if (draft.details.isBlank()) "Details are required" else null,
            singleLine = false,
            contentDesc = "Report details",
            modifier = Modifier.fillMaxWidth().height(140.dp),
        )
        ZenTextField(
            value = draft.notes,
            onValueChange = onNotesChange,
            label = "Additional Notes (optional)",
            placeholder = "Any extra context…",
            singleLine = false,
            contentDesc = "Additional notes",
            modifier = Modifier.fillMaxWidth().height(100.dp),
        )
        CustomFieldsSection(
            fields = draft.customFields,
            onAdd = onAddField,
            onUpdate = onUpdateField,
            onRemove = onRemoveField,
        )
    }
}
