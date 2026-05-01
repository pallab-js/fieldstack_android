package com.fieldstack.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fieldstack.android.ui.theme.LocalElevation
import com.fieldstack.android.ui.theme.Radius
import com.fieldstack.android.ui.theme.Surface as ZenSurface
import com.fieldstack.android.ui.theme.SurfaceBorder

enum class ZenCardVariant { Default, Elevated }

@Composable
fun ZenCard(
    modifier: Modifier = Modifier,
    variant: ZenCardVariant = ZenCardVariant.Default,
    content: @Composable ColumnScope.() -> Unit,
) {
    val elev = LocalElevation.current
    val (radius, padding, shadowDp) = when (variant) {
        ZenCardVariant.Default  -> Triple(Radius.medium, 20.dp, elev.subtle)
        ZenCardVariant.Elevated -> Triple(Radius.large,  24.dp, elev.medium)
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(radius),
        color = ZenSurface,
        shadowElevation = shadowDp,
        border = if (variant == ZenCardVariant.Default)
            BorderStroke(1.dp, SurfaceBorder) else null,
    ) {
        Column(modifier = Modifier.padding(padding), content = content)
    }}
