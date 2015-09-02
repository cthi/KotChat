package com.geomorphology.kotchat.ui

import android.animation.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import com.geomorphology.kotchat.R
import com.geomorphology.kotchat.extension.hasNetworkConnection
import kotlin.properties.Delegates

public class ServerJoinActivity : BaseServiceActivity() {
    companion object {
        public val START_CHAT_VIEW : String = "com.geomorphology.kotchat.START_CHAT_VIEW"
    }

    private var mBg : RelativeLayout by Delegates.notNull()
    private var mNickname : EditText by Delegates.notNull()
    private var mNext : ImageButton by Delegates.notNull()

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val activityIntent = Intent(this@ServerJoinActivity, javaClass<ChannelsActivity>())
            activityIntent.putExtras(intent)
            startActivity(activityIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_join)

        bindViews()
        startBackgroundColorAnimation()
        setupActionDone()
        registerForMessages()
    }

    private fun bindViews() {
        mBg = findViewById(R.id.activity_server_join_bg) as RelativeLayout
        mNickname = findViewById(R.id.activity_server_nickname) as EditText
        mNext = findViewById(R.id.activity_server_done) as ImageButton
    }

    private fun registerForMessages() {
        val broadcastManager  = LocalBroadcastManager.getInstance(this);

        val intentFilter = IntentFilter();
        intentFilter.addAction(START_CHAT_VIEW)

        broadcastManager.registerReceiver(mBroadcastReceiver, intentFilter)
    }

    private fun startBackgroundColorAnimation()  {
        val animSet = AnimatorSet()

        val color1 = getResources().getColor(R.color.transition_green)
        val color2 = getResources().getColor(R.color.transition_teal)
        val color3 = getResources().getColor(R.color.transition_blue)

        val colorAnim1 = ValueAnimator.ofObject(ArgbEvaluator(), color1, color2)
        colorAnim1.addUpdateListener { animator ->
            mBg.setBackgroundColor(animator.getAnimatedValue() as Int)
        }

        val colorAnim2 = ValueAnimator.ofObject(ArgbEvaluator(), color2, color3)
        colorAnim2.addUpdateListener { animator ->
            mBg.setBackgroundColor(animator.getAnimatedValue() as Int)
        }

        val colorAnim3 = ValueAnimator.ofObject(ArgbEvaluator(), color3, color1)
        colorAnim3.addUpdateListener { animator ->
            mBg.setBackgroundColor(animator.getAnimatedValue() as Int)
        }

        animSet.setDuration(5000)
        animSet.playSequentially(colorAnim1, colorAnim2, colorAnim3)

        animSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animSet.start()
            }
        })

        animSet.start()
    }

    private fun setupActionDone() {
        mNickname.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setUsername()
            }
            false
        }

        mNext.setOnClickListener {
            setUsername()
        }
    }

    private fun setUsername() {
        if (hasNetworkConnection()) {
            mService.setUsername(mNickname.getText().toString())
        } else {
            Snackbar.make(mBg, R.string.sb_internet_err, Snackbar.LENGTH_LONG).show()
        }
    }
}
