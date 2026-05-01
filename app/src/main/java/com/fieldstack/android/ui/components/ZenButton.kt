package com.fieldstack.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fieldstack.android.ui.theme.Error
import com.fieldstack.android.ui.theme.ErrorDark
import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.MintDark
import com.fieldstack.android.ui.theme.Radius
import com.fieldstack.android.ui.theme.Stone
import com.fieldstack.android.ui.theme.StoneDeep
import com.fieldstack.android.ui.theme.StoneLight
import androidx.compose.foundation.shape.RoundedCornerShape

enum class ZenButtonVariant { Primary, Secondary, Ghost, Destructive }
enum class ZenButtonSize { Small, Medium, Large }

private data class SizeTokens(val height: Int, val hPad: Int, val vPad: Int, val fontSize: Int)

private fun sizeTokens(size: ZenButtonSize) = when (size) {
    ZenButtonSize.Small  -> SizeTokens(32, 16, 8,  13)
    ZenButtonSize.Medium -> SizeTokens(40, 20, 10, 15)
    ZenButtonSize.Large  -> SizeTokens(48, 28, 12, 17)
}

@Composable
fun ZenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ZenButtonVariant = ZenButtonVariant.Primary,
    size: ZenButtonSize = ZenButtonSize.Medium,
    enabled: Boolean = true,
) {
    val t = sizeTokens(size)
    val shape = RoundedCornerShape(Radius.medium)
    val contentPadding = PaddingValues(horizontal = t.hPad.dp, vertical = t.vPad.dp)
    val label: @Composable () -> Unit = {
        Text(text, fontSize = t.fontSize.sp)
    }
    val mod = modifier
        .height(t.height.dp)
        .semantics {
            role = Role.Button
            if (!enabled) stateDescription = "disabled"
        }

    when (variant) {
        ZenButtonVariant.Primary -> Button(
            onClick = onClick, enabled = enabled, shape = shape,
            contentPadding = contentPadding, modifier = mod,
            colors = ButtonDefaults.buttonColors(
                containerColor = Mint, contentColor = Color.White,
                disabledContainerColor = Mint.copy(alpha = 0.5f),
                disabledContentColor = Color.White.copy(alpha = 0.5f),
            ),
            content = { label() },
        )
        ZenButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick, enabled = enabled, shape = shape,
            contentPadding = contentPadding, modifier = mod,
            border = BorderStroke(1.5.dp, if (enabled) Mint else Mint.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Mint,
                disabledContentColor = Mint.copy(alpha = 0.5f),
            ),
            content = { label() },
        )
        ZenButtonVariant.Ghost -> TextButton(
            onClick = onClick, enabled = enabled, shape = shape,
            contentPadding = contentPadding, modifier = mod,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Stone,
                disabledContentColor = Stone.copy(alpha = 0.5f),
            ),
            content = { label() },
        )
        ZenButtonVariant.Destructive -> Button(
            onClick = onClick, enabled = enabled, shape = shape,
            contentPadding = contentPadding, modifier = mod,
            colors = ButtonDefaults.buttonColors(
                containerColor = Error, contentColor = Color.White,
                disabledContainerColor = Error.copy(alpha = 0.5f),
                disabledContentColor = Color.White.copy(alpha = 0.5f),
            ),
            content = { label() },
        )
    }
}
