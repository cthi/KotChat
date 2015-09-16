package com.geomorphology.kotchat.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.geomorphology.kotchat.R
import com.geomorphology.kotchat.model.Message
import com.geomorphology.kotchat.model.MessageType

public class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private val TYPE_SEND = 0
    private val TYPE_RECV = 1
    private val TYPE_SYS = 2

    private val mContext: Context
    private val mMessages: List<Message>

    constructor(context: Context, messages: List<Message>) {
        this.mContext = context
        this.mMessages = messages;
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is RecvHolder) {
            holder.mMessage.setText(mMessages.get(position).message)
            holder.mSender.setText(mMessages.get(position).sender)
        } else if (holder is SentHolder) {
            holder.mMessage.setText(mMessages.get(position).message)
        } else if (holder is SysHolder) {
            holder.mMessage.setText(mMessages.get(position).message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (mMessages.get(position).type) {
            MessageType.RECV -> return TYPE_RECV
            MessageType.SEND -> return TYPE_SEND
            MessageType.SYS -> return TYPE_SYS
        }
    }

    override fun getItemCount(): Int {
        return mMessages.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        if (viewType == TYPE_SEND) {
            val view = LayoutInflater.from(parent?.getContext()).inflate(R.layout.item_message_sent, parent, false)
            val holder = SentHolder(view)

            holder.mBubble.getBackground().setColorFilter(mContext.getResources().getColor(R.color.reply_grey), PorterDuff.Mode.MULTIPLY)
            return holder
        } else if (viewType == TYPE_RECV) {
            val view = LayoutInflater.from(parent?.getContext()).inflate(R.layout.item_message_recv, parent, false)
            val holder = RecvHolder(view)

            holder.mBubble.getBackground().setColorFilter(mContext.getResources().getColor(R.color.blue_primary), PorterDuff.Mode.MULTIPLY)
            return holder
        } else {
            val view = LayoutInflater.from(parent?.getContext()).inflate(R.layout.item_message_sys, parent, false)
            return SysHolder(view)
        }
    }
}

class RecvHolder : RecyclerView.ViewHolder {
    var mMessage: TextView
    var mSender: TextView
    var mBubble: View

    constructor(view: View) : super(view) {
        mMessage = view.findViewById(R.id.item_message_recv) as TextView
        mSender = view.findViewById(R.id.item_message_recv_sender) as TextView
        mBubble = view.findViewById(R.id.item_message_recv_bubble)
    }
}

class SentHolder : RecyclerView.ViewHolder {
    var mMessage: TextView
    var mBubble: View

    constructor(view: View) : super(view) {
        mMessage = view.findViewById(R.id.item_message_sent) as TextView
        mBubble = view.findViewById(R.id.item_message_sent_bubble)
    }
}

class SysHolder : RecyclerView.ViewHolder {
    var mMessage: TextView

    constructor(view: View) : super(view) {
        mMessage = view.findViewById(R.id.item_message_sys) as TextView
    }
}