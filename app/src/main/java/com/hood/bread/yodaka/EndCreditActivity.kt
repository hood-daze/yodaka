package com.hood.bread.yodaka

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.FragmentActivity
import android.view.KeyEvent
import android.view.View

class EndCreditActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_endroll)

        //http://accelebiz.hatenablog.com/entry/2016/09/02/074545
        //SAM変換
        Handler().postDelayed({
            val intent = Intent(this, TitleActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition (R.anim.med_fadein,
                    R.anim.med_fadeout)
        },5000)

    }

    /*再開のとき始める*/
    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    //戻るキー無効。
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BACK ->
                    return true
            }
        }
        return super.dispatchKeyEvent(event)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {//dialog用
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

}
