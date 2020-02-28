package com.hood.bread.yodaka


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.content.DialogInterface
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import android.view.WindowManager


class Alert : DialogFragment() {

    /*物語を始める確認ダイアログのボタンリスナをメンバー変数に。*/
    inner class DialogButtonClickListener : DialogInterface.OnClickListener{
        //クリック時に呼び出される。一番目の引数には　https://www.javadrive.jp/android/event/index2.html
        override fun onClick(dialog: DialogInterface?, which: Int) {
            val a = activity
            if (a is OnAlertListener){
                a.onAlertClick(which)
            }
        }
    }

    /*ここでアラートダイアログを作り、それぞれのボタンにリスナを実装*/
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        /*ダイアログビルダーを生成。どのアクティビティから呼ばれるかわからないから引数にはactivity*/
        val builder = AlertDialog.Builder(activity).apply {
            setTitle("info")
            setMessage("セーブデータがありません。")
            setCancelable(false)
            setPositiveButton("OK",DialogButtonClickListener())
        }
        /*ダイアログオブジェクトを生成アンドリターン*/
        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

}

