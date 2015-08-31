package com.geomorphology.kotchat.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import com.geomorphology.kotchat.service.ChatService
import kotlin.properties.Delegates

public abstract class BaseServiceActivity : AppCompatActivity() {
    public var mService: ChatService by Delegates.notNull()
    protected var mServiceBound: Boolean by Delegates.notNull()

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ChatService.ChatServiceBinder
            mService = binder.getService()
            mServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mServiceBound = false
        }
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, javaClass<ChatService>())
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }
}
