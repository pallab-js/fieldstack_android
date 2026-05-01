package com.fieldstack.android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.fieldstack.android.ui.theme.BudgetZenTypography
import com.fieldstack.android.ui.theme.Error
import com.fieldstack.android.ui.theme.InputBorder
import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.Radius
import com.fieldstack.android.ui.theme.Stone
import com.fieldstack.android.ui.theme.StoneDark
import com.fieldstack.android.ui.theme.StoneDeep
import com.fieldstack.android.ui.theme.StoneLight
import com.fieldstack.android.ui.theme.WarmGray

@Composable
fun ZenTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    contentDesc: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val isError = errorText != null
    Column(modifier = modifier) {
        if (label != null) {
            Text(label, style = BudgetZenTypography.bodySmall, color = StoneDark)
            Spacer(Modifier.height(6.dp))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            isError = isError,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            placeholder = placeholder?.let { { Text(it, color = WarmGray) } },
            shape = RoundedCornerShape(Radius.medium),
            modifier = Modifier
                .fillMaxWidth()
                .height(if (singleLine) 56.dp else 120.dp)
                .semantics { contentDesc?.let { contentDescription = it } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor    = Mint,
                unfocusedBorderColor  = InputBorder,
                errorBorderColor      = Error,
                disabledBorderColor   = StoneLight,
                disabledContainerColor = StoneLight,
                disabledTextColor     = WarmGray,
                focusedTextColor      = StoneDeep,
                unfocusedTextColor    = StoneDeep,
            ),
            textStyle = BudgetZenTypography.bodySmall,
        )
        if (isError && errorText != null) {
            Spacer(Modifier.height(4.dp))
            Text(errorText, style = BudgetZenTypography.labelSmall, color = Error)
        } else if (helperText != null) {
            Spacer(Modifier.height(4.dp))
            Text(helperText, style = BudgetZenTypography.labelSmall, color = Stone)
        }
    }
}
