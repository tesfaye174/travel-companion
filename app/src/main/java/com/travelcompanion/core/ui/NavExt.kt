package com.travelcompanion.core.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import timber.log.Timber

/**
 * Naviga in sicurezza solo se l'azione è raggiungibile dalla destinazione corrente, evitando crash da race condition.
 */
fun NavController.safeNavigate(
    @IdRes actionId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null
) {
    val current: NavDestination = currentDestination ?: return
    if (current.getAction(actionId) != null || graph.findNode(actionId) != null) {
        try {
            navigate(actionId, args, navOptions)
        } catch (e: IllegalArgumentException) {
            Timber.w(e, "safeNavigate skipped: action %d unreachable from %s", actionId, current.label)
        }
    }
}
