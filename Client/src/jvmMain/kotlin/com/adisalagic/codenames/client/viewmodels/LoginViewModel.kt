package com.adisalagic.codenames.client.viewmodels

import com.adisalagic.codenames.client.api.DisconnectReason
import com.adisalagic.codenames.client.api.Manager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(Data("127.0.0.1:21721", "", ConnectionState.DISCONNECTED))
    val state = _state.asStateFlow()
    val log: Logger = LogManager.getLogger("Login")

    init {
        Manager.setConnectionListener(object : Manager.ConnectionListener {

            override fun onConnectionSuccess(address: String) {
                log.info("Connected to server: $address")
                _state.update { it.copy(connectionState = ConnectionState.CONNECTED) }
            }

            override fun onConnecting() {
                log.info("Starting connection...")
                _state.update { it.copy(connectionState = ConnectionState.CONNECTING) }
            }

            override fun onDisconnect(reason: DisconnectReason) {
                log.info(
                    "Disconnected! ${
                        if (reason == DisconnectReason.SOFT) {
                            " By user"
                        } else {
                            " Something broke"
                        }
                    }"
                )
                _state.update { it.copy(connectionState = ConnectionState.DISCONNECTED) }
            }

        })
    }


    fun update(data: Data){
        _state.update { it.copy(
            address = data.address,
            nickname = data.nickname,
            connectionState = data.connectionState
        ) }
    }
    fun connect(){
        Manager.connect(_state.value.address, _state.value.nickname)
    }
    data class Data(val address: String, val nickname: String, val connectionState: ConnectionState)

    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }
}