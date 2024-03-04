@file:OptIn(ExperimentalPermissionsApi::class)

package com.w2sv.composeutils.permissions.extendedpermissionstate

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.w2sv.composeutils.permissions.extensions.launchMultiplePermissionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Stable
open class ExtendedMultiplePermissionsState(
    private val requestLaunchedBefore: StateFlow<Boolean>,
    multiplePermissionsState: MultiplePermissionsState,
    override val grantedFromRequest: SharedFlow<Boolean>,
    private val onLaunchingSuppressed: () -> Unit = {}
) : MultiplePermissionsState by multiplePermissionsState,
    ExtendedPermissionState {

    override val granted: Boolean by ::allPermissionsGranted

    override fun launchRequest(onSuppressed: (() -> Unit)?) {
        launchMultiplePermissionRequest(
            launchedBefore = requestLaunchedBefore.value,
            onSuppressed = onSuppressed ?: this.onLaunchingSuppressed
        )
    }
}

@SuppressLint("ComposeUnstableCollections")
@Composable
fun rememberExtendedMultiplePermissionsState(
    permissions: List<String>,
    requestLaunchedBefore: StateFlow<Boolean>,
    saveRequestLaunched: () -> Unit,
    onPermissionResult: (Map<String, Boolean>) -> Unit = {},
    onLaunchingSuppressed: () -> Unit = {},
    scope: CoroutineScope = rememberCoroutineScope()
): ExtendedMultiplePermissionsState {

    val grantedFromRequest = remember {
        MutableSharedFlow<Boolean>()
    }

    val permissionState = rememberMultiplePermissionsState(
        permissions = permissions,
        onPermissionsResult = remember {
            { map ->
                if (!requestLaunchedBefore.value) {
                    saveRequestLaunched()
                }
                onPermissionResult(map)
                scope.launch { grantedFromRequest.emit(map.values.all { it }) }
            }
        }
    )

    return remember {
        ExtendedMultiplePermissionsState(
            requestLaunchedBefore = requestLaunchedBefore,
            multiplePermissionsState = permissionState,
            grantedFromRequest = grantedFromRequest,
            onLaunchingSuppressed = onLaunchingSuppressed
        )
    }
}