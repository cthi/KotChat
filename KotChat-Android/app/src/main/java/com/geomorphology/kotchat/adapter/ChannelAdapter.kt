package com.geomorphology.kotchat.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.geomorphology.kotchat.R
import com.geomorphology.kotchat.model.Channel
import com.geomorphology.kotchat.ui.OnChannelSwap

public class ChannelAdapter : RecyclerView.Adapter<ViewHolder> {
    private val mContext : Context
    private val mChannels : List<Channel>
    private val mOnChannelSwap : OnChannelSwap

    constructor(context: Context, onChannelSwap: OnChannelSwap, channels : List<Channel>) {
        this.mContext = context
        this.mChannels = channels
        this.mOnChannelSwap = onChannelSwap
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view = LayoutInflater.from(parent?.getContext()).inflate(R.layout.item_channel, parent, false)
        val holder = ViewHolder(view)

        view.setOnClickListener {
            mOnChannelSwap.onChannelSwap(holder.getAdapterPosition())
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.mChannelName?.setText(mChannels.get(position).channelName)
    }

    override fun getItemCount(): Int {
        return mChannels.count()
    }
}

class ViewHolder : RecyclerView.ViewHolder {
    var mChannelName : TextView

    constructor(view: View) : super(view) {
        mChannelName = view.findViewById(R.id.item_channel_name) as TextView
    }
}
