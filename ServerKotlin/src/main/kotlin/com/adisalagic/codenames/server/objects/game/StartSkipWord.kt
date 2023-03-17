package com.adisalagic.codenames.server.objects.game

import com.adisalagic.codenames.server.BaseAPI
import com.adisalagic.codenames.server.objects.Event

data class StartSkipWord(val duration: Int): BaseAPI(Event.GAME_START_SKIP_WORD)