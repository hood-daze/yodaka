package com.hood.bread.yodaka

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

/*フラグメントライフサイクル　参照
* https://developer.android.com/guide/components/fragments?hl=ja#Lifecycle
* */

class LogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.fragment_log)
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        return  dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog.findViewById<TextView>(R.id.text_log_content).text = arguments!!.getString("log","")
        dialog.findViewById<Button>(R.id.button_log_back).setOnClickListener{
            val a =activity
            if(a is View.OnClickListener){
                a.onClick(it)
                dismiss()
            }
        }

        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }
}