package com.cna.mineru.cna.Utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment

import com.cna.mineru.cna.R

class PhotoDialog : DialogFragment() {
    private var mDialogResult: OnMyDialogResult? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val dialog = inflater?.inflate(R.layout.dialog_camera, null, false)
        val btn_camera = dialog?.findViewById(R.id.btn_camera) as TextView
        val btn_gallery = dialog?.findViewById(R.id.btn_gallery) as TextView

        btn_camera.setOnClickListener {
            mDialogResult!!.finish(1)
            dismiss()
        }

        btn_gallery.setOnClickListener {
            mDialogResult!!.finish(2)
            dismiss()
        }

        builder.setView(dialog)
        return builder.create()
    }


    fun setDialogResult(dialogResult: OnMyDialogResult) {
        mDialogResult = dialogResult
    }

    interface OnMyDialogResult {
        fun finish(result: Int)
    }

}
