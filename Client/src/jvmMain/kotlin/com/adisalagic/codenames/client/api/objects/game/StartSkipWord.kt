package com.adisalagic.codenames.client.api.objects.game

import com.adisalagic.codenames.client.api.BaseAPI
import com.adisalagic.codenames.client.api.objects.Event


data class StartSkipWord(val duration: Int): BaseAPI(Event.GAME_START_OPEN_WORD)