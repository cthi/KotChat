package com.geomorphology.kotchat.ui

import android.content.res.Configuration
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.EditText
import android.widget.ImageButton
import com.geomorphology.kotchat.R
import com.geomorphology.kotchat.adapter.ChannelAdapter
import com.geomorphology.kotchat.adapter.MessageAdapter
import com.geomorphology.kotchat.model.Channel
import com.geomorphology.kotchat.model.Message
import com.geomorphology.kotchat.transport.MessageListObservable
import java.util.ArrayList
import kotlin.properties.Delegates

public class ChannelsActivity : BaseServiceActivity(), OnChannelSwap {
    private var mToolbar: Toolbar by Delegates.notNull()
    private var mNavView: RecyclerView by Delegates.notNull()
    private var mDrawerLayout: DrawerLayout by Delegates.notNull()
    private var mDrawerToggle: ActionBarDrawerToggle by Delegates.notNull()
    private var mRecyclerView: RecyclerView by Delegates.notNull()
    private var mSendText: EditText by Delegates.notNull()

    private val mMessages = ArrayList<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BaseServiceActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)

        bindViews()
        setupToolbar()
        setupRecyclerView()
        setupNavView()
        subscribeForMessages()
        registerSendClick()
    }

    private fun bindViews() {
        mToolbar = findViewById(R.id.toolbar) as Toolbar
        mNavView = findViewById(R.id.drawer_list) as RecyclerView
        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        mRecyclerView = findViewById(R.id.channel_rv) as RecyclerView
        mSendText = findViewById(R.id.item_message_sent) as EditText
    }

    private fun setupToolbar() {
        setSupportActionBar(mToolbar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        mRecyclerView.setLayoutManager(LinearLayoutManager(this))
        mRecyclerView.setAdapter(MessageAdapter(this, mMessages))

        val con = findViewById(R.id.enter_text_container)
        con?.getBackground()?.setColorFilter(getResources().getColor(R.color.reply_grey), PorterDuff.Mode.MULTIPLY)
    }

    private fun setupNavView() {
        mDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name)
        mDrawerToggle.setDrawerIndicatorEnabled(true)
        mDrawerLayout.setDrawerListener(mDrawerToggle)

        // Mock
        var list = listOf(Channel("#Test1", 1), Channel("#Test2", 2), Channel("#Test3", 3))
        var channelAdapter = ChannelAdapter(this, this, list)
        //

        mNavView.setLayoutManager(LinearLayoutManager(this))
        mNavView.setAdapter(channelAdapter)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super<BaseServiceActivity>.onPostCreate(savedInstanceState)
        mDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super<BaseServiceActivity>.onConfigurationChanged(newConfig)
        mDrawerToggle.onConfigurationChanged(newConfig)
    }

    private fun subscribeForMessages() {
        MessageListObservable.getObservable().subscribe { message ->
            runOnUiThread {
                mMessages.add(message)

                mRecyclerView.getAdapter().notifyItemInserted(mMessages.count())
                mRecyclerView.scrollToPosition(mMessages.count() - 1)
            }
        }
    }

    private fun registerSendClick() {
        val sendButton = findViewById(R.id.item_message_send_btn) as ImageButton

        sendButton.setOnClickListener {
            val content = mSendText.getText().toString()

            if (!content.isEmpty()) {
                mService.sendMessage(mSendText.getText().toString())
                mSendText.setText("")
            }
        }

    }

    override fun onChannelSwap(channelId: Int) {
        mService.joinRoom(channelId)
    }
}

interface OnChannelSwap {
    fun onChannelSwap(channelId: Int)
}
