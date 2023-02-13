package com.adisalagic.codenames.server.objects.game

import com.adisalagic.codenames.server.BaseAPI

data class TimerInfo(val timer: String, val timeStamp: String): BaseAPI("game_timer")
