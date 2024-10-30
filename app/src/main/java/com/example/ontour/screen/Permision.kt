package com.example.ontour.screen

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LocationPermissionButton() {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            // Handle permission granted scenario


        } else {



        }
    }

        // Button to request permission
        LaunchedEffect(Unit) {
            val isAlreadyGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED

            if (isAlreadyGranted) {
                permissionGranted = true
            } else {
                // Automatically request the location permission when entering the app
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

}
