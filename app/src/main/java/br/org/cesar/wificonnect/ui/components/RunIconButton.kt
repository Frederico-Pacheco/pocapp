package br.org.cesar.wificonnect.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun RunIconButton(
    isRunning: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    IconButton(
        onClick = {
            if (!isRunning) onClick()
        },
        enabled = enabled
    ) {
        if (isRunning) {
            CircularProgressIndicator()
        } else {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Run Use Case"
            )
        }
    }
}