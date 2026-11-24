package com.zaurh.bober.ui

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * Google Play in-app updates (flexible): a new version downloads in the background while the app is
 * used, then [updateReady] turns true so the UI can offer a restart. Only works for Play installs;
 * elsewhere (debug builds, sideloads) the check fails quietly.
 *
 * Create in `onCreate` (it registers an activity result launcher).
 */
class InAppUpdates(activity: ComponentActivity) {

    private val manager: AppUpdateManager = AppUpdateManagerFactory.create(activity)
    private val launcher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { }

    var updateReady by mutableStateOf(false)
        private set

    private val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) updateReady = true
    }

    fun check() {
        manager.registerListener(listener)
        manager.appUpdateInfo
            .addOnSuccessListener { info ->
                when {
                    info.installStatus() == InstallStatus.DOWNLOADED -> updateReady = true
                    info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) ->
                        manager.startUpdateFlowForResult(info, launcher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
                }
            }
            .addOnFailureListener { Log.d(TAG, "Update check unavailable: ${it.message}") }
    }

    /** Re-checks when returning to the app: a download may have finished meanwhile. */
    fun onResume() {
        manager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.installStatus() == InstallStatus.DOWNLOADED) updateReady = true
        }
    }

    /** Installs the downloaded update; Play restarts the app. */
    fun install() {
        updateReady = false
        manager.completeUpdate()
    }

    fun later() {
        updateReady = false
    }

    fun release() {
        manager.unregisterListener(listener)
    }

    private companion object {
        const val TAG = "InAppUpdates"
    }
}
