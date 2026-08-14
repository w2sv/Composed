@file:OptIn(ExperimentalPermissionsApi::class)

package com.w2sv.composed.permissions.extendedpermissionstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.w2sv.composed.permissions.extensions.launchPermissionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Permission state which, as opposed to the accompanist [PermissionState], which it extends
 * - exposes a [grantedFromRequest] shared flow to allow for distributed subscription and callback invocation, instead of only being able to pass a onPermissionResult callback upon instantiation, which needs to cover all granting reactions, possibly impacting various components
 * - allows for callbacks upon permission requesting being suppressed
 *
 * Discerning whether launching is indeed suppressed requires a persisted boolean value, representing whether the permission request has already been launched at least once, which is to be passed as StateFlow under [requestLaunchedBefore].
 */
@Stable
open class ExtendedSinglePermissionState(
    private val requestLaunchedBefore: StateFlow<Boolean>,
    permissionState: PermissionState,
    override val grantedFromRequest: SharedFlow<Boolean>,
    private val defaultOnLaunchingSuppressed: () -> Unit = {}
) : PermissionState by permissionState,
    ExtendedPermissionState {

    override val granted by status::isGranted

    override fun launchRequest(onSuppressed: (() -> Unit)?) {
        launchPermissionRequest(
            launchedBefore = requestLaunchedBefore.value,
            onSuppressed = onSuppressed ?: defaultOnLaunchingSuppressed
        )
    }
}

@Composable
fun rememberExtendedSinglePermissionState(
    permission: String,
    requestLaunchedBefore: StateFlow<Boolean>,
    saveRequestLaunched: () -> Unit,
    defaultOnPermissionResult: (Boolean) -> Unit = {},
    defaultOnLaunchingSuppressed: () -> Unit = {},
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
                defaultOnPermissionResult(it)
                scope.launch { grantedFromRequest.emit(it) }
            }
        }
    )

    return remember {
        ExtendedSinglePermissionState(
            requestLaunchedBefore = requestLaunchedBefore,
            permissionState = permissionState,
            grantedFromRequest = grantedFromRequest,
            defaultOnLaunchingSuppressed = defaultOnLaunchingSuppressed
        )
    }
}
