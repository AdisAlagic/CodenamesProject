package com.adisalagic.codenames.client.utils

import com.goxr3plus.streamplayer.enums.Status
import com.goxr3plus.streamplayer.stream.StreamPlayer
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent
import com.goxr3plus.streamplayer.stream.StreamPlayerListener
import org.apache.logging.log4j.LogManager
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.logging.Level
import java.util.logging.Logger

object SoundPlayer {
    private val logger = LogManager.getLogger(this::class.simpleName)
    const val GAME_START = "game_start.mp3"
    const val GAME_LOG_SEND = "game_log_send.mp3"

    private val player = HiddenPlayer()

    fun playSound(sound: String){
        val stream = this::class.java.classLoader.getResourceAsStream(sound)
        if (stream == null){
            logger.error("Sound file not found! Can't play sound")
            return
        }
        val bufferedInputStream = BufferedInputStream(stream)
        player.playSound(bufferedInputStream)
    }
}

class HiddenPlayer : StreamPlayer(Logger.getGlobal().apply { level = Level.OFF }) {
    private val logger = LogManager.getLogger(this::class.simpleName)

    private val streamPlayerListener = object : StreamPlayerListener {
        override fun opened(dataSource: Any?, properties: MutableMap<String, Any>?) {
            logger.debug("Opened music file: $dataSource")
        }

        override fun progress(
            nEncodedBytes: Int,
            microsecondPosition: Long,
            pcmData: ByteArray?,
            properties: MutableMap<String, Any>?
        ) {

        }

        override fun statusUpdated(event: StreamPlayerEvent?) {
            val status = event?.playerStatus ?: return
            when (status) {
                Status.INIT -> logger.debug("Player inited")
                Status.OPENED -> logger.debug("Player ready")
                Status.STOPPED -> logger.debug("Player stopped")
                Status.PAUSED -> logger.debug("Player paused")
                Status.RESUMED -> logger.debug("Player resumed")
                Status.EOM -> logger.debug("Sound is over")
                Status.NOT_SPECIFIED,
                Status.OPENING,
                Status.PLAYING,
                Status.SEEKING,
                Status.BUFFERING,
                Status.SEEKED,
                Status.PAN,
                Status.GAIN -> {}
            }
        }

    }

    init {
        try {
            addStreamPlayerListener(streamPlayerListener)
        }catch (e: Exception) {
            logger.warn("Player had an error: $e")
        }
    }

    fun playSound(file: InputStream){
        try {
            open(file)
            play()
        }catch (e: Exception){
            logger.warn("Player had an error: $e")
        }
    }
}

