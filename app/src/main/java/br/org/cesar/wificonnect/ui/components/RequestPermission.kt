package br.org.cesar.wificonnect.ui.components

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestPermission(
    permission: String,
    permissionStatus: Int,
    onPermissionChange: (Int) -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val permissionResult = ContextCompat.checkSelfPermission(
            context, permission
        )
        onPermissionChange(permissionResult)
    }

    LaunchedEffect(permissionStatus) {
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(permission)
        }
    }
}