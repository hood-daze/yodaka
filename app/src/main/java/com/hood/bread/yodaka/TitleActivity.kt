package com.hood.bread.yodaka


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button


//change AppCompatActivity
class TitleActivity : FragmentActivity(), OnAlertListener, View.OnClickListener, DialogInterface.OnClickListener{

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.credit_back->{
                soundPool!!.play(soundBack, 1.0f, 1.0f, 0, 0, 1f)
            }
        }
    }

    override fun onAlertClick(which: Int) {
        when(which){
            DialogInterface.BUTTON_POSITIVE -> {
                soundPool!!.play(soundBack, 1.0f, 1.0f, 0, 0, 1f)

                findViewById<Button>(R.id.button_start).isEnabled=true
                findViewById<Button>(R.id.button_cont).isEnabled=true
                findViewById<Button>(R.id.go_credit).isEnabled=true
            }
        }
    }

    private var bgTitle: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private var soundGo: Int = 0
    private var soundBack: Int = 0
    private var isNew: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*splashからの遷移かどうか判断。真ならスタート画面、偽ならタイトル画面を見せる。*/
        var isSplash = intent.getBooleanExtra("isSplash", false)
        if (isSplash) {
            setContentView(R.layout.activity_start)
        } else {
            setContentView(R.layout.activity_title)
        }

        /*音楽設定*/
        bgTitle = MediaPlayer.create(this, R.raw.bgm_title)
        bgTitle!!.setVolume(0.7f, 0.7f)
        bgTitle!!.isLooping = true

        /*se設定:soundGoとsoundBackだけ。*/
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        soundPool = SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(2)
                .build()
        soundGo = soundPool!!.load(this, R.raw.se_go, 1)//waveのロード
        soundBack = soundPool!!.load(this, R.raw.se_back, 1)


    }

    /*activityが止るときbgmを止める*/
    override fun onPause() {
        super.onPause()
        bgTitle!!.pause()
    }

    /*activityが始まるときまた再開されるときbgmを鳴らす*/
    override fun onResume() {
        super.onResume()
        bgTitle!!.start()
        hideSystemUI()
    }

    /*activityが終わるときbgmを止める*/
    override fun onDestroy() {
        super.onDestroy()
        bgTitle!!.release()// メモリの解放
        bgTitle = null // 音楽プレーヤーを破棄
    }

    /*スタート画面のボタンTap*/
    fun tapStart(view: View) {
        soundPool!!.play(soundGo, 1.0f, 1.0f, 0, 0, 1f)
        setContentView(R.layout.activity_title)
    }

    fun startNewStory(view: View) {
        //他押せなくする。
        findViewById<Button>(R.id.button_start).isEnabled=false
        findViewById<Button>(R.id.button_cont).isEnabled=false
        findViewById<Button>(R.id.go_credit).isEnabled=false

        isNew = true
        soundPool!!.play(soundGo, 1.0f, 1.0f, 0, 0, 1f)
        val storyConf = StoryConfirmDialog()
        // https://stackoverflow.com/questions/15459209/passing-argument-to-dialogfragment :値の受け渡し
        val args = Bundle()
        args.putBoolean("isNew",isNew)
        storyConf.arguments = args
        storyConf.show(supportFragmentManager,"Confirm Story")
    }

    fun startContStory(view: View) {
        isNew = false
        soundPool!!.play(soundGo, 1.0f, 1.0f, 0, 0, 1f)
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        //データを読み込み、あったらconfirmする
        val i = sharedPref.getInt("save", -1)
        if (i == -1){
            findViewById<Button>(R.id.button_start).isEnabled=false
            findViewById<Button>(R.id.button_cont).isEnabled=false
            findViewById<Button>(R.id.go_credit).isEnabled=false

            val alt = Alert()
            alt.show(supportFragmentManager,"AlertInfo")
        }else{
            findViewById<Button>(R.id.button_start).isEnabled=false
            findViewById<Button>(R.id.button_cont).isEnabled=false
            findViewById<Button>(R.id.go_credit).isEnabled=false

            val storyConf = StoryConfirmDialog()
            val args = Bundle()
            args.putBoolean("isNew",isNew)
            storyConf.arguments = args
            storyConf.show(supportFragmentManager,"Confirm Story")
        }
    }

    /*StoryConfirmDialogでポジティブボタンを押したときの処理。*/
    override fun onClick(dialog: DialogInterface?, which: Int) {

        when(which){
            DialogInterface.BUTTON_POSITIVE->{
                soundPool!!.play(soundGo, 1.0f, 1.0f, 0, 0, 1f)
                val i = Intent(this,StoryActivity::class.java)
                if(isNew){
                    i.putExtra("stage",0)
                }else{
                    val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    val sNum = sharedPref.getInt("save", -1)
                    i.putExtra("stage",sNum)
                }

                val fadeOut = AnimationUtils.loadAnimation(this,R.anim.med_fadeout)
                fadeOut.setAnimationListener(object :SimpleAnimationListener(){
                    override fun onAnimationStart(animation: Animation) {
                        super.onAnimationStart(animation)
                        //押せなくする処理。
                        findViewById<Button>(R.id.button_start).isEnabled=false
                        findViewById<Button>(R.id.button_cont).isEnabled=false
                        findViewById<Button>(R.id.go_credit).isEnabled=false
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        super.onAnimationEnd(animation)
                        startActivity(i)
                        finish()
                    }
                })
                (window.decorView as ViewGroup).getChildAt(0).startAnimation(fadeOut)
            }

            DialogInterface.BUTTON_NEGATIVE->{
                soundPool!!.play(soundBack, 1.0f, 1.0f, 0, 0, 1f)
                //おせるようにする処理。
                findViewById<Button>(R.id.button_start).isEnabled=true
                findViewById<Button>(R.id.button_cont).isEnabled=true
                findViewById<Button>(R.id.go_credit).isEnabled=true
            }
        }
    }


    fun goCredit(view: View){
        soundPool!!.play(soundGo, 1.0f, 1.0f, 0, 0, 1f)
        val credit = CreditDialog()
        credit.show(supportFragmentManager, "credit")
    }

    //戻るキー無効。
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BACK -> return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    //システムバーを無くすメソッド
    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {//dialog用
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }
}

