package com.hood.bread.yodaka


import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.content.DialogInterface
import androidx.fragment.app.DialogFragment
import android.view.WindowManager


class ExitConfirmDialog : DialogFragment() {

    inner class DialogButtonClickListener :DialogInterface.OnClickListener{
        override fun onClick(dialog: DialogInterface?, which: Int) {
            val a =activity
            if (a is DialogInterface.OnClickListener){
                a.onClick(dialog, which)
            }
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder =AlertDialog.Builder(activity).apply {
            setTitle("退出するとデータが上書きされます。")
            setMessage("物語を終わりますか？")
            setPositiveButton("はい",DialogButtonClickListener())
            setNegativeButton("いいえ",DialogButtonClickListener())
        }
        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

}
