package com.w2sv.composeutils.permissions.extendedpermissionstate

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.SharedFlow

@Stable
interface ExtendedPermissionState {
    val granted: Boolean
    val grantedFromRequest: SharedFlow<Boolean>

    fun launchRequest(onSuppressed: (() -> Unit)? = null)
}