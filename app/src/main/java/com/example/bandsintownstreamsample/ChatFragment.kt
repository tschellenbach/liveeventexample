package com.example.bandsintownstreamsample

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bandsintownstreamsample.databinding.FragmentChatBinding
import com.getstream.sdk.chat.viewmodel.bindView
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.getstream.sdk.chat.viewmodel.messages.bindView
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.utils.ChatUtils

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: LivePlayerViewModel by viewModels()

    private var userNameDialog: AlertDialog? = null

    // We get this from the previous activity which makes an api call to our backend
    // TODO: insert your own user id
    private val chatUserId: String = "summer-brook-2"
    // TODO: insert your own user token
    private val chatUserToken: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoic3VtbWVyLWJyb29rLTIifQ.CzyOx8kgrc61qVbzWvhV1WD3KPEo5ZFZH-326hIdKz0"

    private val chatChannelId: String = "sandbox5"
    private val chatChannelType = "livestream"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        binding.joinChat.setOnClickListener {
            userNameDialog = createUserNameDialog(chatUserId, chatUserToken).apply { show() }
        }

        return binding.root
    }


    private fun createUserNameDialog(chatUserId: String, chatUserToken: String): AlertDialog {
        val editText = EditText(requireContext()).apply {
            hint = "Enter your username"
            isSingleLine = true
        }

        val dialogListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    if (editText.text.isBlank()) {
                        Toast.makeText(requireContext(), "Name can't be empty", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val userName = editText.text.toString()
                        val streamListener = object : OnStreamUserConnectListener {
                            override fun onSuccess() = setupChatViews()
                            override fun onError(error: ChatError) {
                                Toast.makeText(
                                    requireContext(),
                                    "An error occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        viewModel.initStreamUser(
                            chatUserId,
                            chatUserToken,
                            userName,
                            streamListener
                        )
                        dialog.dismiss()
                    }
                }
                DialogInterface.BUTTON_NEGATIVE -> dialog.cancel()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(editText)
            .setTitle("You'll be chatting as")
            .setPositiveButton(android.R.string.ok, dialogListener)
            .setNegativeButton(android.R.string.cancel, dialogListener)
            .create()
    }

    private fun setupChatViews() {
        binding.joinChat.visibility = View.GONE
        binding.messageListBlurred.visibility = View.GONE
        binding.messageList.visibility = View.VISIBLE
        binding.messageInput.visibility = View.VISIBLE

        val channelId = "$chatChannelType:$chatChannelId"
        val streamViewModels = viewModel.createMessageViewModels(this, channelId)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                streamViewModels.listViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
            }
        })

        streamViewModels.listViewModel.apply {
            bindView(binding.messageList, viewLifecycleOwner)
            state.observe(viewLifecycleOwner) {
                if (it is MessageListViewModel.State.NavigateUp) {
                    activity?.finish()
                }
            }
        }

        streamViewModels.inputViewModel.apply {
            bindView(binding.messageInput, viewLifecycleOwner)
            streamViewModels.listViewModel.mode.observe(viewLifecycleOwner) {
                when (it) {
                    is MessageListViewModel.Mode.Thread -> setActiveThread(it.parentMessage)
                    is MessageListViewModel.Mode.Normal -> resetThread()
                }
            }
            binding.messageList.setOnMessageEditHandler {
                editMessage.postValue(it)
            }
        }
    }
}