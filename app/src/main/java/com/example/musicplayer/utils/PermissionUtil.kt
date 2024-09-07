package com.example.musicplayer.utils

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.util.Log

object PermissionUtil {

    fun Fragment.checkAndAskPermission(onPermissionGranted: () -> Unit) {
        val permission = if (Build.VERSION.SDK_INT >= TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if
                (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onPermissionGranted()
        } else {
            Toast.makeText(
                requireContext(),
                "This permission is needed for storage.",
                Toast.LENGTH_SHORT
            ).show()
            requestPermission(permission, onPermissionGranted)
        }
    }

    private fun Fragment.requestPermission(permission: String, onPermissionGranted: () -> Unit) {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                if (!shouldShowRequestPermissionRationale(permission)) {
                    Toast.makeText(
                        requireContext(),
                        "Please allow Storage Permission it in app settings.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
                openAppSettings()
            }
        }.launch(permission)
    }

    private fun Fragment.openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
            data = uri
        }
        startActivity(intent)
    }
}
