package com.adisalagic.codenames.server.objects.game

import com.adisalagic.codenames.server.BaseAPI
import com.adisalagic.codenames.server.objects.Event

data class TimerInfo(val timer: ULong, val timeStamp: String): BaseAPI(Event.GAME_TIMER)
