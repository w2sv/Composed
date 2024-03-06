package com.w2sv.composed.permissions.extendedpermissionstate

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.SharedFlow

/**
 * Permission state which, as opposed to the accompanist ones,
 * - exposes a [grantedFromRequest] shared flow to allow for distributed subscription and callback invocation, instead of only being able to pass a onPermissionResult callback upon instantiation, which needs to cover all granting reactions, possibly impacting various components
 * - allows for callbacks upon permission requesting being suppressed
 */
@Stable
interface ExtendedPermissionState {
    val granted: Boolean

    /**
     * The result of a launched permission request.
     */
    val grantedFromRequest: SharedFlow<Boolean>

    /**
     * Launches the permission request if launching is not suppressed, otherwise invokes [onSuppressed].
     */
    fun launchRequest(onSuppressed: (() -> Unit)? = null)
}