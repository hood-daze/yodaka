package com.hood.bread.yodaka


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.content.DialogInterface
import androidx.fragment.app.DialogFragment
import android.view.WindowManager


class StoryConfirmDialog : DialogFragment() {

    /*物語を始める確認ダイアログのボタンリスナをメンバー変数に。*/
    inner class DialogButtonClickListener : DialogInterface.OnClickListener{
        override fun onClick(dialog: DialogInterface?, which: Int) {
            val a = activity
            if (a is DialogInterface.OnClickListener) {
                a.onClick(dialog, which)
            }
        }
    }

    /*ここでアラートダイアログを作り、それぞれのボタンにリスナを実装*/
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        /*ダイアログビルダーを生成。どのアクティビティから呼ばれるかわからないから引数にはactivity*/
        val builder = AlertDialog.Builder(activity).apply {
            val isNew = arguments!!.getBoolean("isNew")
            if (isNew){
                setTitle("確認")
                setMessage("はじめから物語を開始します。よろしいですか？")
            }else{
                setTitle("確認")
                setMessage("前回の続きから物語を開始します。よろしいですか？")
            }
            setPositiveButton("はい",DialogButtonClickListener())
            setNegativeButton("いいえ",DialogButtonClickListener())
            setCancelable(false)
        }
        /*ダイアログオブジェクトを生成アンドリターン*/
        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

}
