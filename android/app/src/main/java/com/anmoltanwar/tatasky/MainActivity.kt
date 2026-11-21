package com.anmoltanwar.tatasky

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.tv.TvContract
import android.os.Bundle

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences("dth_input", MODE_PRIVATE)
        val port = preferences.getInt("hdmi_port", 0)

        if (port == 0) {
            showPortSelector(preferences)
        } else {
            window.decorView.viewTreeObserver.addOnPreDrawListener { false }
            switchToHdmi(port)
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun showPortSelector(preferences: SharedPreferences) {
        val labels = Array(4) { getString(R.string.hdmi_port, it + 1) }
        AlertDialog.Builder(this)
            .setTitle(R.string.choose_hdmi)
            .setItems(labels) { _, position ->
                // Save before closing. The first launch never switches inputs.
                preferences.edit()
                    .putInt("hdmi_port", position + 1)
                    .remove("input_id")
                    .commit()
                finishAndRemoveTask()
            }
            .setOnCancelListener { finishAndRemoveTask() }
            .show()
    }

    private fun switchToHdmi(port: Int) {
        // Fixed Hisense/MediaTek mapping: HDMI 1 -> HW4, HDMI 2 -> HW5, etc.
        // Reference: https://gist.github.com/zehuanli/158f0420040f5dee4b11a30ac64d9c1d
        // HDMI 4 uses HW7 by the same numbering; no firmware or input checks are made.
        val inputId = "com.mediatek.tvinput/.hdmi.HDMIInputService/HW${port + 3}"
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                TvContract.buildChannelUriForPassthroughInput(inputId),
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
        finishAndRemoveTask()
    }
}
