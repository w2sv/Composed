package com.w2sv.composeutils.permissions.extendedpermissionstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.w2sv.composeutils.permissions.extensions.launchPermissionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Stable
@OptIn(ExperimentalPermissionsApi::class)
open class ExtendedSinglePermissionState(
    private val requestLaunchedBefore: StateFlow<Boolean>,
    permissionState: PermissionState,
    override val grantedFromRequest: SharedFlow<Boolean>,
    private val onLaunchingSuppressed: () -> Unit = {}
) : PermissionState by permissionState,
    ExtendedPermissionState {

    override val granted by status::isGranted

    override fun launchRequest(onSuppressed: (() -> Unit)?) {
        launchPermissionRequest(
            launchedBefore = requestLaunchedBefore.value,
            onSuppressed = onSuppressed ?: this.onLaunchingSuppressed
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberExtendedPermissionState(
    permission: String,
    requestLaunchedBefore: StateFlow<Boolean>,
    saveRequestLaunched: () -> Unit,
    onPermissionResult: (Boolean) -> Unit = {},
    scope: CoroutineScope = rememberCoroutineScope()
): ExtendedSinglePermissionState {

    val grantedFromRequest = remember {
        MutableSharedFlow<Boolean>()
    }

    val permissionState = rememberPermissionState(
        permission = permission,
        onPermissionResult = remember {
            {
                if (!requestLaunchedBefore.value) {
                    saveRequestLaunched()
                }
                onPermissionResult(it)
                scope.launch { grantedFromRequest.emit(it) }
            }
        }
    )

    return remember {
        ExtendedSinglePermissionState(
            requestLaunchedBefore = requestLaunchedBefore,
            permissionState = permissionState,
            grantedFromRequest = grantedFromRequest
        )
    }
}