package com.adisalagic.codenames

import com.adisalagic.codenames.server.ServerThread
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.reflect.KClass

object Logger {
    fun getLogger(clazz: KClass<out Any>): Logger {
        return LogManager.getLogger(clazz.simpleName)
    }
}