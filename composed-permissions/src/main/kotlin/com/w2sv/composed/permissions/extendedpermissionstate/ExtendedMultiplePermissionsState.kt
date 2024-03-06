@file:OptIn(ExperimentalPermissionsApi::class)

package com.w2sv.composed.permissions.extendedpermissionstate

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.w2sv.composed.permissions.extensions.launchMultiplePermissionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Permission state which, as opposed to the accompanist [MultiplePermissionsState], which it extends
 * - exposes a [grantedFromRequest] shared flow to allow for distributed subscription and callback invocation, instead of only being able to pass a onPermissionResult callback upon instantiation, which needs to cover all granting reactions, possibly impacting various components
 * - allows for callbacks upon permission requesting being suppressed
 *
 * Discerning whether launching is indeed suppressed requires a persisted boolean value, representing whether the permission request has already been launched at least once, which is to be passed as StateFlow under [requestLaunchedBefore].
 */
@Stable
open class ExtendedMultiplePermissionsState(
    private val requestLaunchedBefore: StateFlow<Boolean>,
    multiplePermissionsState: MultiplePermissionsState,
    override val grantedFromRequest: SharedFlow<Boolean>,
    private val defaultOnLaunchingSuppressed: () -> Unit = {}
) : MultiplePermissionsState by multiplePermissionsState,
    ExtendedPermissionState {

    override val granted: Boolean by ::allPermissionsGranted

    override fun launchRequest(onSuppressed: (() -> Unit)?) {
        launchMultiplePermissionRequest(
            launchedBefore = requestLaunchedBefore.value,
            onSuppressed = onSuppressed ?: defaultOnLaunchingSuppressed
        )
    }
}

@SuppressLint("ComposeUnstableCollections")
@Composable
fun rememberExtendedMultiplePermissionsState(
    permissions: List<String>,
    requestLaunchedBefore: StateFlow<Boolean>,
    saveRequestLaunched: () -> Unit,
    defaultOnPermissionResult: (Map<String, Boolean>) -> Unit = {},
    defaultOnLaunchingSuppressed: () -> Unit = {},
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
                defaultOnPermissionResult(map)
                scope.launch { grantedFromRequest.emit(map.values.all { it }) }
            }
        }
    )

    return remember {
        ExtendedMultiplePermissionsState(
            requestLaunchedBefore = requestLaunchedBefore,
            multiplePermissionsState = permissionState,
            grantedFromRequest = grantedFromRequest,
            defaultOnLaunchingSuppressed = defaultOnLaunchingSuppressed
        )
    }
}