package com.adisalagic.codenames.client.utils

import com.adisalagic.codenames.client.api.Manager
import org.apache.logging.log4j.LogManager
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

class CountDownTimer(
    private val duration: Duration,
    val eventTriggerInterval: Duration,
    val onTick: (Duration) -> Unit
) {
    var countDownTimer: Manager.EventTimer? = null
    val logger = LogManager.getLogger(this::class.simpleName)

    fun start(): CountDownTimer {
        stop()
        val all = duration.toInt(DurationUnit.MILLISECONDS)
        var est = 0
        countDownTimer = Manager.EventTimer {
            logger.debug("Progress: $est / $all")
            onTick(est.milliseconds)
            if (est == all){
                Manager.removeTimeListener(countDownTimer!!)
            }
            est++
        }
        Manager.addTimeListener(countDownTimer!!)
        logger.debug("Subscribing")
        return this
    }

    fun stop(): CountDownTimer {
        if (countDownTimer != null) {
            Manager.removeTimeListener(countDownTimer!!)
        }
        return this
    }

    fun end(): CountDownTimer {
        onTick(duration)
        stop()
        return this
    }

    fun reset(): CountDownTimer {
        onTick(ZERO)
        return this
    }
}