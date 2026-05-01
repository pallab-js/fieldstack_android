package com.fieldstack.android.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldstack.android.ui.components.ZenButton
import com.fieldstack.android.ui.components.ZenButtonVariant
import com.fieldstack.android.ui.components.ZenTextField
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is LoginUiState.Success) onLoginSuccess()
    }

    val isLoading = state is LoginUiState.Loading
    val errorMsg  = (state as? LoginUiState.Error)?.message

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.sm)
            .semantics(mergeDescendants = true) { contentDescription = "Login screen" },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("FieldStack", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(4.dp))
        Text("Calm, focused field operations", style = MaterialTheme.typography.bodyMedium, color = Stone)
        Spacer(Modifier.height(Spacing.xl))

        ZenTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "you@fieldstack.com",
            errorText = if (errorMsg?.contains("email", ignoreCase = true) == true) errorMsg else null,
            enabled = !isLoading,
            contentDesc = "Email input",
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        )
        Spacer(Modifier.height(Spacing.xs))

        ZenTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "••••••••",
            errorText = if (errorMsg?.contains("password", ignoreCase = true) == true) errorMsg else null,
            enabled = !isLoading,
            contentDesc = "Password input",
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                viewModel.login(email, password)
            }),
        )

        // Generic error (not field-specific)
        if (errorMsg != null && !errorMsg.contains("email", ignoreCase = true)
            && !errorMsg.contains("password", ignoreCase = true)) {
            Spacer(Modifier.height(6.dp))
            Text(errorMsg, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(Spacing.md))

        ZenButton(
            text = if (isLoading) "Signing in…" else "Sign In",
            onClick = { viewModel.login(email, password) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
