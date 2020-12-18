package com.example.bandsintownstreamsample

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.factory.ChannelViewModelFactory
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.events.ConnectedEvent
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.socket.InitConnectionListener
import io.getstream.chat.android.livedata.ChatDomain
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class LivePlayerViewModel : ViewModel() {

    fun initStreamUser(
        userId: String,
        userToken: String,
        displayName: String,
        onStreamUserConnectListener: OnStreamUserConnectListener? = null,
    ) {
        val user = User(userId).apply {
            extraData["name"] = displayName
        }

        if (ChatClient.instance().getCurrentUser() != null) {
            onStreamUserConnectListener?.onSuccess()
        } else {

            ChatClient.instance().setUser(user, userToken, object : InitConnectionListener() {
                override fun onSuccess(data: ConnectionData) {

                        onStreamUserConnectListener?.onSuccess()
                }

                override fun onError(error: ChatError) {
                    Log.d(TAG, "Error: ${error.message}")
                    onStreamUserConnectListener?.onError(error)
                }
            })
        }
    }

    fun createMessageViewModels(
        owner: ViewModelStoreOwner,
        channelId: String
    ): StreamMessageViewModels {
        val viewModelProvider = ViewModelProvider(owner, ChannelViewModelFactory(channelId))
        val listViewModel = viewModelProvider.get(MessageListViewModel::class.java)
        val inputViewModel = viewModelProvider.get(MessageInputViewModel::class.java)
        return StreamMessageViewModels(listViewModel, inputViewModel)
    }

    override fun onCleared() {
        ChatClient.instance().disconnect()
    }

    companion object {
        private val TAG = LivePlayerViewModel::class.java.simpleName
    }
}

interface OnStreamUserConnectListener {
    fun onSuccess()
    fun onError(error: ChatError)
}