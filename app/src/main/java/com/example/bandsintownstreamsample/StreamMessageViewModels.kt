package com.example.bandsintownstreamsample

import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel

data class StreamMessageViewModels(
        val listViewModel: MessageListViewModel,
        val inputViewModel: MessageInputViewModel
)
