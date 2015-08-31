package com.geomorphology.kotchat

import android.app.Application
import android.content.Intent
import com.geomorphology.kotchat.service.ChatService

public class KotChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startService(Intent(this, javaClass<ChatService>()))
    }
}
