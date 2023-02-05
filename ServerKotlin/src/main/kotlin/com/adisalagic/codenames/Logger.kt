package com.adisalagic.codenames

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.LoggerConfig
import kotlin.reflect.KClass


object Logger {
    fun getLogger(clazz: KClass<out Any>): Logger {
        return LogManager.getLogger(clazz.simpleName)
    }

    init {
        updateLoggersLevel(false)
    }

    fun updateLoggersLevel(isDebug: Boolean){
        val ctx = LogManager.getContext(false) as LoggerContext
        val config = ctx.configuration
        val loggerConfig: LoggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME)
        if (isDebug){
            loggerConfig.level = Level.ALL
            ctx.updateLoggers()
        }else{
            loggerConfig.level = Level.INFO
            ctx.updateLoggers()
        }
        getLogger(this::class).debug("Logger has been updated. Is debug = $isDebug")
    }
}