@file:OptIn(ExperimentalPermissionsApi::class)

package com.w2sv.composeutils.permissions.extensions

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.shouldShowRationale

fun PermissionState.isLaunchingSuppressed(launchedBefore: Boolean): Boolean =
    !status.shouldShowRationale && launchedBefore

inline fun PermissionState.launchPermissionRequest(
    launchedBefore: Boolean,
    onSuppressed: () -> Unit
) {
    if (!isLaunchingSuppressed(launchedBefore))
        launchPermissionRequest()
    else
        onSuppressed()
}

fun MultiplePermissionsState.isLaunchingSuppressed(launchedBefore: Boolean): Boolean =
    !shouldShowRationale && launchedBefore

inline fun MultiplePermissionsState.launchMultiplePermissionRequest(
    launchedBefore: Boolean,
    onSuppressed: () -> Unit
) {
    if (!isLaunchingSuppressed(launchedBefore))
        launchMultiplePermissionRequest()
    else
        onSuppressed()
}