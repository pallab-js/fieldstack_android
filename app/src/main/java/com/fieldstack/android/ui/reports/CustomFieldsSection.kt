package com.fieldstack.android.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.fieldstack.android.domain.model.CustomField
import com.fieldstack.android.domain.model.CustomFieldType
import com.fieldstack.android.ui.components.ZenButton
import com.fieldstack.android.ui.components.ZenButtonVariant
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.components.ZenFilterChip
import com.fieldstack.android.ui.components.ZenTextField
import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone
import java.util.UUID

@Composable
fun CustomFieldsSection(
    fields: List<CustomField>,
    onAdd: (CustomField) -> Unit,
    onUpdate: (String, String) -> Unit,
    onRemove: (String) -> Unit,
) {
    var showAddForm by remember { mutableStateOf(false) }
    var newLabel by remember { mutableStateOf("") }
    var newType by remember { mutableStateOf(CustomFieldType.Text) }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Custom Fields", style = MaterialTheme.typography.labelMedium, color = Stone)
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { showAddForm = !showAddForm },
                modifier = Modifier.semantics { contentDescription = "Add custom field" },
            ) {
                Icon(Icons.Default.Add, null, tint = Mint)
            }
        }

        // Existing fields
        fields.forEach { field ->
            ZenCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(field.label, style = MaterialTheme.typography.bodySmall, color = Stone)
                        Spacer(Modifier.height(4.dp))
                        when (field.type) {
                            CustomFieldType.Checkbox -> Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = field.value == "true",
                                    onCheckedChange = { onUpdate(field.id, it.toString()) },
                                    colors = CheckboxDefaults.colors(checkedColor = Mint),
                                    modifier = Modifier.semantics { contentDescription = field.label },
                                )
                                Text(if (field.value == "true") "Yes" else "No",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            else -> ZenTextField(
                                value = field.value,
                                onValueChange = { onUpdate(field.id, it) },
                                placeholder = "Enter ${field.label.lowercase()}…",
                                contentDesc = field.label,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    IconButton(
                        onClick = { onRemove(field.id) },
                        modifier = Modifier.semantics { contentDescription = "Remove ${field.label}" },
                    ) {
                        Icon(Icons.Default.Close, null, tint = Stone)
                    }
                }
            }
        }

        // Add field form
        if (showAddForm) {
            ZenCard(modifier = Modifier.fillMaxWidth()) {
                Text("New Field", style = MaterialTheme.typography.labelMedium, color = Stone)
                Spacer(Modifier.height(6.dp))
                HorizontalDivider()
                Spacer(Modifier.height(6.dp))
                ZenTextField(
                    value = newLabel,
                    onValueChange = { if (it.length <= 64) newLabel = it },
                    label = "Field Label",
                    placeholder = "e.g. Serial Number",
                    contentDesc = "New field label",
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(6.dp))
                Text("Type", style = MaterialTheme.typography.bodySmall, color = Stone)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    CustomFieldType.entries.forEach { t ->
                        ZenFilterChip(
                            label = t.name,
                            selected = newType == t,
                            onClick = { newType = t },
                        )
                    }
                }
                Spacer(Modifier.height(Spacing.xs))
                ZenButton(
                    text = "Add Field",
                    onClick = {
                        if (newLabel.isNotBlank()) {
                            onAdd(CustomField(UUID.randomUUID().toString(), newLabel, newType))
                            newLabel = ""
                            showAddForm = false
                        }
                    },
                    enabled = newLabel.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                )
                ZenButton(
                    text = "Cancel",
                    onClick = { showAddForm = false; newLabel = "" },
                    variant = ZenButtonVariant.Ghost,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
