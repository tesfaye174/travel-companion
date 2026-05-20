package com.travelcompanion.core.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import timber.log.Timber

/**
 * Safely navigates using an action id only if the action is reachable from the
 * current destination. Prevents IllegalArgumentException crashes when the user
 * taps a button while the NavController is on a different destination (e.g.
 * during a bottom-nav tab switch or a double-tap race).
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
