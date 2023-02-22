package com.adisalagic.codenames.server.objects

class Role private constructor() {
    companion object {
        const val SPECTATOR = 0;
        const val PLAYER = 1;
        const val MASTER = 2;
    }
}

class Team private constructor() {
    companion object {
        const val RED = 100
        const val BLUE = 101
        const val NONE = 102
    }
}

class Side private constructor() {
    companion object {
        const val RED = 200
        const val BLUE = 201
        const val WHITE = 202
        const val BLACK = 203
    }
}

class State private constructor() {
    companion object {
        const val STATE_NOT_STARTED = 300
        const val STATE_PLAYING = 301
        const val STATE_PAUSED = 302
        const val STATE_ENDED = 303
    }
}

class Event private constructor() {
    companion object {
        const val GAME_STATE = 1000
        const val GAME_PLAYER_INFO = 1001
        const val GAME_PLAYER_LIST = 1002
        const val GAME_START_OPEN_WORD = 1003
        const val GAME_TIMER = 1004

        const val REQUEST_JOIN = 2000
        const val REQUEST_JOIN_TEAM = 2001
        const val REQUEST_PAUSE_RESUME = 2002
        const val REQUEST_PRESS_WORD = 2003
        const val REQUEST_RESTART = 2004
        const val REQUEST_SEND_LOG = 2005
        const val REQUEST_SHUFFLE_TEAMS = 2006
        const val REQUEST_TIMER = 2007
    }
}