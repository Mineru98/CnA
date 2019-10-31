package com.cna.mineru.cna.Utils

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

import com.cna.mineru.cna.R

class PhotoDialog2 : DialogFragment() {
    private var mDialogResult: OnMyDialogResult? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = activity?.let { AlertDialog.Builder(it) }
        val inflater = activity?.layoutInflater
        val dialog = inflater?.inflate(R.layout.dialog_camera2, null, false)
        val btn_camera = dialog?.findViewById(R.id.btn_camera) as TextView
        val btn_gallery = dialog.findViewById(R.id.btn_gallery) as TextView
        val btn_editor = dialog.findViewById(R.id.btn_editor) as TextView

        btn_camera.setOnClickListener {
            mDialogResult!!.finish(1)
            dismiss()
        }

        btn_gallery.setOnClickListener {
            mDialogResult!!.finish(2)
            dismiss()
        }

        btn_editor.setOnClickListener {
            mDialogResult!!.finish(3)
            dismiss()
        }

        builder?.setView(dialog)
        return builder!!.create()
    }


    fun setDialogResult(dialogResult: OnMyDialogResult) {
        mDialogResult = dialogResult
    }

    interface OnMyDialogResult {
        fun finish(result: Int)
    }

}
