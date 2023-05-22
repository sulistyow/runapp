package com.liztstudio.runtime.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.liztstudio.runtime.R

class CancelTrackingDialog : DialogFragment() {

    private var yesListener: (() -> Unit)? = null

    fun setYesListener(listener: () -> Unit) {
        yesListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancel your Run?")
            .setMessage("Are you sure cancel current runt and delete it's data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("yes") { _, _ ->
                yesListener?.let { yes ->
                    yes()
                }
            }
            .setNegativeButton("No") { dialogInterface, i ->
                dialogInterface.cancel()
            }
            .create()
    }
}