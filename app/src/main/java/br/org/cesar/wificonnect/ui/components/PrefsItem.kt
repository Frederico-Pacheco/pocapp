package br.org.cesar.wificonnect.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PrefsItem(
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {},
    text: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (icon != null) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                icon()
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            applyTextStyle(text)?.invoke()
            applySecondaryTextStyle(secondaryText)?.invoke()
        }

        trailing()
    }
}

private fun applyTextStyle(
    content: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    if (content == null) return null
    return {
        val newTextStyle = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Normal
        )
        CompositionLocalProvider(
            LocalTextStyle provides newTextStyle
        ) {
            content()
        }
    }
}

private fun applySecondaryTextStyle(
    content: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    if (content == null) return null
    return {
        val newTextStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Thin
        )
        CompositionLocalProvider(
            LocalTextStyle provides newTextStyle
        ) {
            content()
        }
    }
}

