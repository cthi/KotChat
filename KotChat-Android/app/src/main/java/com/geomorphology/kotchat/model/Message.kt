package com.geomorphology.kotchat.model

data class Message(val message : String,
                        val sender : String?,
                        val type : MessageType);

enum class MessageType {
    RECV, SEND, SYS
}
