package com.w2sv.composeutils.permissions.extensions

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
fun PermissionState.isLaunchingSuppressed(launchedBefore: Boolean): Boolean =
    !status.shouldShowRationale && launchedBefore

@OptIn(ExperimentalPermissionsApi::class)
fun PermissionState.launchPermissionRequest(launchedBefore: Boolean, onSuppressed: () -> Unit) {
    if (!isLaunchingSuppressed(launchedBefore))
        launchPermissionRequest()
    else
        onSuppressed()
}

@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.isLaunchingSuppressed(launchedBefore: Boolean): Boolean =
    !shouldShowRationale && launchedBefore

@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.launchMultiplePermissionRequest(
    launchedBefore: Boolean,
    onSuppressed: () -> Unit
) {
    if (!isLaunchingSuppressed(launchedBefore))
        launchMultiplePermissionRequest()
    else
        onSuppressed()
}