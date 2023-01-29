package com.adisalagic.codenames.client.viewmodels

import androidx.compose.runtime.collectAsState
import com.adisalagic.codenames.client.api.BaseAPI
import com.adisalagic.codenames.client.api.DisconnectReason
import com.adisalagic.codenames.client.api.Manager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.logging.Logger


class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(Data("127.0.0.1:21721", "", ConnectionState.DISCONNECTED))
    val state = _state.asStateFlow()
    val log = Logger.getLogger("Login")

    init {
        Manager.setMessageListener(object : Manager.Listener {
            override fun onMessageReceive(msg: String) {
                log.info { "Message received: $msg" }
            }

            override fun onConnectionSuccess(address: String) {
                log.info { "Connected to server: $address" }
                _state.update { it.copy(connectionState = ConnectionState.CONNECTED) }
            }

            override fun onConnecting() {
                log.info { "Starting connection..." }
                _state.update { it.copy(connectionState = ConnectionState.CONNECTING) }
            }

            override fun onDisconnect(reason: DisconnectReason) {
                log.info {
                    "Disconnected! ${
                        if (reason == DisconnectReason.SOFT) {
                            " By user"
                        } else {
                            " Something broke"
                        }
                    }"
                }
                _state.update { it.copy(connectionState = ConnectionState.DISCONNECTED) }
            }

        })
    }
    fun sendHello() {
        viewModelScope.launch {
            Manager.sendMessage(BaseAPI("kek"))
        }
    }

    fun update(data: Data){
        _state.update { it.copy(
            address = data.address,
            nickname = data.nickname,
            connectionState = data.connectionState
        ) }
    }
    fun connect(){
        Manager.connect(state.value.address, state.value.nickname)
    }
    data class Data(val address: String, val nickname: String, val connectionState: ConnectionState)

    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }
}