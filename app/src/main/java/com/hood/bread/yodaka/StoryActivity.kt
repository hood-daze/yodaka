package com.hood.bread.yodaka

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
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
import android.widget.ImageView
import android.widget.TextView


class StoryActivity : FragmentActivity(), DialogInterface.OnClickListener, View.OnClickListener {

    //log画面から戻る
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_log_back -> {
                soundPool!!.play(soundBack, 1.0f, 1.0f, 0, 0, 1f)
            }

        }
    }

    /*退出確認ダイアログでオーケーを押した場合の処理*/
    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                soundPool!!.play(soundGo, 1.0f, 1.0f, 0, 0, 1f)
                save()
                storyFadeout()
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                soundPool!!.play(soundBack, 1.0f, 1.0f, 0, 0, 1f)
                storyButtonEnable(true)
            }
        }

    }

    private var _isFirst = true

    internal var bgm: MediaPlayer? = null
    internal var soundPool: SoundPool? = null
    internal var soundGo: Int = 0
    internal var soundBack: Int = 0
    /*internal var soundBashi: Int = 0
    internal var soundHakken: Int = 0*/

    internal var soundBasa: Int = 0

    private var bgDraw: Drawable? = null

    private var message: Button? = null
    private var name: TextView? = null
    private var place: TextView? = null
    private var chara: ImageView? = null
    private var item: ImageView? = null
    private var bg: ImageView? = null

    //name:body:me:kuchi:bg:bgm:se:item:こんな調子です。
    private val name_list: MutableList<String> = mutableListOf()
    private val body_list: MutableList<String> = mutableListOf()
    private val me_list: MutableList<String> = mutableListOf()
    private val kuchi_list: MutableList<String> = mutableListOf()
    private val bg_list: MutableList<String> = mutableListOf()
    private val bgm_list: MutableList<String> = mutableListOf()
    private val se_list: MutableList<String> = mutableListOf()
    private val item_list: MutableList<String> = mutableListOf()
    private val str_list: MutableList<String> = mutableListOf()
    private val log_list: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        chapCount = intent.getIntExtra("stage", 0)

        /*サウンドエフェクトの設定。*/
        setSE()
        /*オーディオマネージャーについて。*/
        //https://developer.android.com/reference/android/media/AudioManager*/
        /*ルートビューとトップビューに色を与える*/
        var decorView = window.decorView
        decorView.setBackgroundColor(Color.BLACK)
        var rootView = (decorView as ViewGroup).getChildAt(0)
        rootView.setBackgroundColor(Color.WHITE)

        message = findViewById(R.id.message)
        place = findViewById(R.id.place)
        name = findViewById(R.id.chara_name)
        chara = findViewById(R.id.chara)
        item = findViewById(R.id.item)
        bg = findViewById(R.id.bg)

        //stringのテストをする。
        //改行で分割。
        val sentences = readtext().split("\n")
        //空のリストにaddしていく。例
        //name:body:me:kuchi:bg:bgm:se:item:こんな調子です。
        for (sentence in sentences) {
            val s= sentence.split(":")
            name_list.add(s[0])
            body_list.add(s[1])
            me_list.add(s[2])
            kuchi_list.add(s[3])
            bg_list.add(s[4])
            bgm_list.add(s[5])
            se_list.add(s[6])
            item_list.add(s[7])
            str_list.add(s[8])
        }
        //continueのさい、bgとbgmとitemをいじる必要あり。
        cont()
        advance()
        _isFirst = false
    }

    private fun cont(){
        //もし最初かつbgの中身がなければ、入れる。
        if(_isFirst && bg_list[chapCount]=="bg"){
            for (i in chapCount downTo 0){
                if (bg_list[i]!="bg"){
                    bg_list[chapCount] = bg_list[i]
                    break
                }
            }
        }

        //最初かつ、bgmの中身がないときendだったらbreak、違ったら中身入れる。
        if(_isFirst && bgm_list[chapCount]=="bgm"){
            for (i in chapCount downTo 0){
                if (bgm_list[i]!="bgm"){//
                    if (bgm_list[i] == "end"){
                        break
                    }else{
                        bgm_list[chapCount] = bgm_list[i]
                        break
                    }
                }
            }
        }

        if(_isFirst && item_list[chapCount]=="bgm"){
            for (i in chapCount downTo 0){
                if (item_list[i]!="bgm"){//
                    if (item_list[i] == "end"){
                        break
                    }else{
                        item_list[chapCount] = item_list[i]
                        break
                    }
                }
            }
        }

    }

    private fun readtext(): String {
        //106文字入る。
        //UTF-8にしないとけない。メモ帳をUTF-8で保存すればよい。
        //https://qiita.com/droibit/items/75416c0955b797931bb8
        return assets.open("yodaka.txt").reader(charset = Charsets.UTF_8).use { it.readText() }
    }

    private fun setSE() {
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        soundPool = SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(4)
                .build()
        soundGo = soundPool!!.load(this, R.raw.se_go, 1)//waveのロード
        soundBack = soundPool!!.load(this, R.raw.se_back, 1)
        soundBasa = soundPool!!.load(this, R.raw.se_basa, 1)
        //今回は使わない。
        /*soundBashi = soundPool!!.load(this, R.raw.se_bashi, 1)
        soundHakken = soundPool!!.load(this, R.raw.se_hakken, 1)*/
    }


    private fun advance() {
        bg()
        if (bg_list[chapCount] == "bg") {
            excAdvnce()
        }
    }

    private fun excAdvnce() {
        place()
        mes()
        val nameText = name_list[chapCount]
        if (nameText == ("name") || nameText == ("name_body")) {
            name?.text = null
        } else {
            name!!.text = name_list[chapCount]
        }
        chara()
        item()
        se()
        bgm()
    }

    private fun mes() {
        message!!.text = str_list[chapCount]
        val s: String
        s = if ((name_list[chapCount] == "name") || (name_list[chapCount] == "name_body"))"---" else "---"+name_list[chapCount]

        if (log_list.size < 7){
            log_list.add(s+"\n"+str_list[chapCount])
        }else{
            log_list.add(s+"\n"+str_list[chapCount])
            log_list.removeAt(0)
        }

    }

    /*ボタンが押されたとき、ストーリーを進める。*/
    fun message(view: View) {
        chapCount++
        tapCount++
        if (bg_list[chapCount] == "end") {//物語の最後だったら
            storyEnd()
            //storyFadeout()今回はなし。
        } else {
            advance()
        }
    }

    private fun save() {
        //appidを使い、一意の共有ファイルを使う。
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        //新たな番号を追加(上書き)する。saveをキーにして、番号を格納。
        with(sharedPref.edit()){
            putInt("save", chapCount)
            apply()
        }
    }

    private fun bgm() {

        when {//今回風の音だけ？あとで変える。
            bgm_list[chapCount] =="bgm" ->{}
            bgm_list[chapCount] == "end" -> bgm!!.stop()
            else -> {//bgmを変える場合。
                if (bgm != null) {
                    bgm!!.release()
                    bgm = null
                }
                bgm = MediaPlayer.create(applicationContext, resources.getIdentifier(bgm_list[chapCount],"raw",packageName))
                bgm!!.isLooping = true
                bgm!!.start()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (bgm != null) bgm!!.start()
        hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
        bgm!!.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        bgm!!.release()
        bgm = null
        soundPool!!.release()
        soundPool = null

        tapCount = 0
        chapCount = 0
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

    private fun pictInvisible() {
        chara!!.setImageDrawable(null)
        message!!.text = null
        place!!.text = null
        name!!.text = null
        item()
    }

    private fun bg() {
        /*アニメの設定*/
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.med_fadeout)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.med_fadein)

        fadeOut.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationStart(animation: Animation) {
                storyButtonEnable(false)
                pictInvisible()
            }

            override fun onAnimationEnd(animation: Animation) {
                (window.decorView as ViewGroup).getChildAt(0).startAnimation(fadeIn)//遷移開始
            }
        })
        //fadeinするときに入れる。
        fadeIn.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationStart(animation: Animation) {
                bgDraw = resources.getDrawable(resources.getIdentifier(bg_list[chapCount],"drawable",packageName), theme)
                bg!!.setImageDrawable(bgDraw)
            }

            override fun onAnimationEnd(animation: Animation) {
                storyButtonEnable(true)
                excAdvnce()
            }
        })

        /*アニメの適応*/
        if (bg_list[chapCount] != "bg") {
            if (_isFirst) {
                (window.decorView as ViewGroup).getChildAt(0).startAnimation(fadeIn)
            } else (window.decorView as ViewGroup).getChildAt(0).startAnimation(fadeOut)
        }

    }

    private fun item() {
        when {
            item_list[chapCount] == "item" -> {
            }
            item_list[chapCount] == "end" ->
                item!!.setImageDrawable(null)
            else -> {
                val itemDraw = resources.getDrawable(resources.getIdentifier(item_list[chapCount],"drawable",packageName), theme)
                item!!.setImageDrawable(itemDraw)
            }
        }
    }

    private fun chara() {
        when {
            body_list[chapCount] == "body" -> chara!!.setImageDrawable(null)
            else -> {
                //ここをいじっていく。
                //第二引数入れれば、drawable無しでもいけた。whenで弾く必要あり。.は斜線にする必要あり。とっちゃおう
                val body: Drawable = resources.getDrawable(resources.getIdentifier(body_list[chapCount], "drawable",packageName), theme)
                val me: Drawable = resources.getDrawable(resources.getIdentifier(me_list[chapCount], "drawable",packageName), theme)
                val kuchi: Drawable = resources.getDrawable(resources.getIdentifier(kuchi_list[chapCount], "drawable",packageName), theme)

                val layers = arrayOf(body, me, kuchi)
                val layerDrawable = LayerDrawable(layers)
                chara!!.setImageDrawable(layerDrawable)
            }
        }
    }


    private fun se() {
        //se事前に読み込む必要あるからただ単にseIds[chapCount]が1だったらこれ。2だったらこれという方針。
        when {
            se_list[chapCount] == "se" -> {
            }
            se_list[chapCount] == "se_basa"-> soundPool!!.play(soundBasa, 1.0f, 1.0f, 0, 0, 1f)
        }
    }

    private fun place() {
        /*廃棄予定*/
        /*when {
            IDs.placeIds[chapCount] == 0 -> {
            }
            IDs.placeIds[chapCount] == 1 -> place!!.text = null
            else -> place!!.setText(IDs.placeIds[chapCount])
        }*/
    }

    fun goExitConf(view: View) {
        //ここでmenu画面を出す。
        soundPool!!.play(soundGo, 1.0f, 1.0f, 0, 0, 1f)
        storyButtonEnable(false)
        val exitConf = ExitConfirmDialog()
        exitConf.show(supportFragmentManager, "Confirm Exit")
    }

    fun goLog(view: View) {
        soundPool!!.play(soundGo, 1.0f, 1.0f, 0, 0, 1f)
        val logFragment = LogFragment()
        //logに文字列を送る。
        val bf = StringBuffer()
        for(item in log_list){
            bf.append(item)
            bf.append("\n")
        }

        var s = String(bf)
        var args = Bundle()
        args.putString("log",s)
        logFragment.arguments = args
        logFragment.show(supportFragmentManager, "backlog")
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

    /*xmlのstoryFadeoutを参照。*/
    private fun storyFadeout() {
        val storyFadeout = AnimationUtils.loadAnimation(this, R.anim.long_fadeout)
        storyFadeout!!.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationStart(animation: Animation) {
                pictInvisible()
                storyButtonEnable(false)
            }

            override fun onAnimationEnd(animation: Animation) {
                val intent = Intent(this@StoryActivity, TitleActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        (window.decorView as ViewGroup).getChildAt(0).startAnimation(storyFadeout)
    }

    private fun storyEnd() {
        val storyFadeout = AnimationUtils.loadAnimation(this, R.anim.long_fadeout)
        storyFadeout!!.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationStart(animation: Animation) {
                pictInvisible()
                storyButtonEnable(false)
            }

            override fun onAnimationEnd(animation: Animation) {
                val intent = Intent(this@StoryActivity, EndCreditActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        (window.decorView as ViewGroup).getChildAt(0).startAnimation(storyFadeout)
    }

    private fun storyButtonEnable(clickable: Boolean) {
        findViewById<Button>(R.id.menu).isClickable = clickable
        findViewById<Button>(R.id.Log).isClickable = clickable
        findViewById<Button>(R.id.message).isClickable = clickable
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {//dialog用
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    companion object {
        var tapCount = 0
        var chapCount: Int = 0
    }


}
