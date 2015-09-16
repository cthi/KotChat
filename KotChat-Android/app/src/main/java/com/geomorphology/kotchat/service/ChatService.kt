package com.geomorphology.kotchat.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import com.geomorphology.kotchat.model.*
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.geomorphology.kotchat.transport.MessageListObservable
import com.geomorphology.kotchat.ui.ServerJoinActivity
import org.json.JSONObject

public class ChatService : Service() {
    private val mBinder = ChatServiceBinder()
    private val mSocket: Socket? = IO.socket("http://192.168.0.14:3000")

    inner class ChatServiceBinder : Binder() {
        fun getService(): ChatService {
            return this@ChatService
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (null == intent) {
            stopSelf();
        }

        mSocket?.connect()
        registerSocketCallbacks()

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun registerSocketCallbacks() {
        mSocket?.on(Socket.EVENT_CONNECT, { args ->
        })

        mSocket?.on("connection message", { args ->
        });

        mSocket?.on("chat message", { args ->
            val messageJson = JSONObject(args[0].toString())
            val message = Message(messageJson.getString("message"), messageJson.getString("sender"), MessageType.RECV)

            MessageListObservable.postMessage(message)
        })

        mSocket?.on("user joined", { args ->
            val userJson = JSONObject(args[0].toString())
            val message = Message(userJson.getString("message") + " has joined.", null, MessageType.SYS)

            MessageListObservable.postMessage(message)
        })

        mSocket?.on("user left", { args ->
            val userJson = JSONObject(args[0].toString())
            val message = Message(userJson.getString("message") + " has left.", null, MessageType.SYS)

            MessageListObservable.postMessage(message)
        })

        mSocket?.on("room join message", { args ->
            val userJson = JSONObject(args[0].toString())
            val message = Message(userJson.getString("message"), null, MessageType.SYS)

            MessageListObservable.postMessage(message)
        })

        mSocket?.on("username set", { args ->
            val resultJson = args[0].toString()

            if (JSONObject(resultJson).getBoolean("result")) {
                val intent = Intent(ServerJoinActivity.START_CHAT_VIEW)

                intent.putExtra("rooms", resultJson)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
        })
    }

    public fun setUsername(username: String) {
        var usernameJson = JSONObject()
        usernameJson.put("username", username)

        mSocket?.emit("set username", usernameJson)
    }

    public fun joinRoom(roomNum: Int) {
        val roomJson = JSONObject()
        roomJson.put("roomNum", roomNum)

        mSocket?.emit("join room", roomJson);
    }

    public fun sendMessage(message: String) {
        var messageJson = JSONObject()
        messageJson.put("message", message)

        mSocket?.emit("send message", messageJson)

        MessageListObservable.postMessage(Message(message, null, MessageType.SEND))
    }
}

