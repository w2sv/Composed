package com.w2sv.composeutils.permissions.extendedpermissionstate

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.SharedFlow

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