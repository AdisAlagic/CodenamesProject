package com.adisalagic.codenames.server.objects.game


import com.adisalagic.codenames.server.BaseAPI
import com.adisalagic.codenames.server.objects.Event


data class PlayerList(
    val users: List<PlayerInfo.User>
) : BaseAPI(Event.GAME_PLAYER_LIST)