package com.hood.bread.yodaka

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView

//projectを変えて、リファクタリングするときのやりかた。
//https://google-developer-training.github.io/android-developer-fundamentals-course-concepts-v2/appendix/appendix-utilities/appendix-utilities.html#copy_project

// git公式サイト https://git-scm.com/　　progitがよい。
//githubのremoteに関して削除もできるっぽい。https://help.github.com/articles/adding-a-remote/


class SplashActivity : FragmentActivity() {
    private var mCountFadeout = 0
    private var mCountFadein = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashGroup = findViewById<ConstraintLayout>(R.id.splash_group)//textと画像どちらもとれる
        val groupText = findViewById<View>(R.id.group_text)//textのみ

        val fadein = AnimationUtils.loadAnimation(this, R.anim.short_fadein)
        val fadeout = AnimationUtils.loadAnimation(this, R.anim.short_fadeout)

        fadein.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationEnd(animation: Animation) {
                mCountFadein++
                if (mCountFadein <= 2) {
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {

                    }
                    splashGroup.startAnimation(fadeout)
                }
            }
        })

        fadeout.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationEnd(animation: Animation) {
                mCountFadeout++
                when(mCountFadeout){
                    1->{
                        groupText.visibility = View.INVISIBLE
                        findViewById<ImageView>(R.id.image_logo).setImageResource(R.drawable.logo)
                        splashGroup.startAnimation(fadein)
                    }
                    2->{
                        try {
                            Thread.sleep(500)
                        } catch (e: InterruptedException) {

                        }
                        val intent = Intent(this@SplashActivity, TitleActivity::class.java)
                        intent.putExtra("isSplash", true)
                        startActivity(intent)
                    }

                }
            }
        })
        splashGroup.startAnimation(fadein)

    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        //アクションバーを含めたアクティビティ全体をビューとして取得することもできます。
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    //戻るキー無効。
    @SuppressLint("RestrictedApi")
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
    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }


}
