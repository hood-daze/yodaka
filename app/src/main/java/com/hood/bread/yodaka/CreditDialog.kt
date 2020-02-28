package com.hood.bread.yodaka

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button


class CreditDialog: DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.fragment_credit)
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.findViewById<Button>(R.id.credit_back).setOnClickListener{
            val a=activity
            if (a is View.OnClickListener){
                a.onClick(it)
                dismiss()
            }
        }
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

}