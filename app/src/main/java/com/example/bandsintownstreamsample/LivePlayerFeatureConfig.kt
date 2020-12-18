package com.example.bandsintownstreamsample

import android.content.Context
import com.getstream.sdk.chat.ChatUI
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.livedata.ChatDomain

class LivePlayerFeatureConfig(
    private val context: Context,
) {
    init {
        val client = ChatClient.Builder(STREAM_API_KEY, context)
            .logLevel(ChatLogLevel.ALL)
            .build()

        ChatDomain.Builder(client, context).build()
        ChatUI.Builder(context).build()
    }

    companion object {
        const val STREAM_API_KEY = "b67pax5b2wdq"
    }
}