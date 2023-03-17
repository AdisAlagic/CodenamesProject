package com.adisalagic.codenames.server.configuration

import com.adisalagic.codenames.Logger
import java.io.File
import java.io.InputStreamReader
import kotlin.system.exitProcess

object ConfigurationManager {
    private val logger = Logger.getLogger(this::class)
    var dictionary: List<CharSequence> = emptyList()
    var config: Configuration = Configuration.DEFAULT
        get() {
            return field
        }
        private set(value) {
            field = value
        }

    fun loadDefaultConfig() {
        loadConfig("config.txt")
    }

    fun loadConfig(fileName: String) {
        val file = File(fileName)
        if (!file.exists()) {
            logger.warn("This is probably the first launch. Change config at ${file.absolutePath} and restart the server")
            logger.debug("Config file does not exists. Creating default one...")
            val writer = file.bufferedWriter()
            file.createNewFile()
            try {
                writer.write(
                    """
                debug=${config.debug}
                host=${config.host}
                ip=${config.ip}
                port=${config.port}
                dictionary=${config.dictionary}
                """.trimIndent()
                )
            } catch (e: Exception) {
                logger.error("Something broke when trying to write file:\n${e.message}")
            } finally {
                writer.flush()
                writer.close()
            }
        }
        readConfig(file)
        logger.info("Host name: ${config.host}")
        val dict = File(config.dictionary)
        if (!dict.exists()) {
            logger.debug("Dictionary does not exists. Creating new one...")
            dict.createNewFile()
            val out = dict.outputStream()
            try {
                val stream = this::class.java.classLoader.getResourceAsStream("dict.txt")
                val decoded = InputStreamReader(stream!!).readLines()
                val writer = dict.bufferedWriter()
                decoded.forEach { writer.write("$it\n") }
                writer.apply { flush(); close() }
            }catch (e: Exception){
                logger.error("Something broke when creating new dictionary:\n${e}")
            }finally {
                out.flush()
                out.close()
            }
        }
        val arr = mutableListOf<Byte>()
        dict.inputStream().use {
            do {
                val buffer = ByteArray(1024)
                val bytes = it.read(buffer, 0, buffer.size)
                arr.addAll(buffer.asList())
            }while (bytes != -1)
        }
        val ba = ByteArray(arr.size){return@ByteArray arr[it]}
        dictionary = String(ba, 0, ba.size).replace("\r", "").split('\n')
        if (dictionary.size < 36){
            logger.error("Dictionary must be at least 36 words. Words must be one word per line. Words read: ${dictionary.size}")
            exitProcess(0)
        }
        logger.info("Configuration loading success! Loaded ${dictionary.size} words")
    }

    private fun readConfig(conf: File) {
        val input = conf.bufferedReader().readLines()
        input.forEach {
            val splitted = it.split("=")
            try {
                when (splitted[0]) {
                    "host" -> config = config.copy(host = splitted.getOrElse(1) { "" })
                    "ip" -> config = config.copy(ip = splitted.getOrElse(1) { "" })
                    "port" -> config = config.copy(port = splitted[1].toInt())
                    "dictionary" -> config = config.copy(dictionary = splitted[1])
                    "debug" -> {
                        config = config.copy(debug = splitted[1].toBooleanStrict())
                        Logger.updateLoggersLevel(config.debug)
                    }
                }
                logger.debug("Reading config line: $it")
            } catch (e: Exception) {
                logger.warn("Configuration were not read:\n${e.message}\nUsing default")
                config = Configuration.DEFAULT
            }
        }
    }
}