package com.adisalagic.codenames.server.configuration

data class Configuration(
    val host: String = "",
    val port: Int = 21721,
    val ip: String = "",
    val debug: Boolean = false,
    val dictionary: String
){
    companion object{
        val DEFAULT = Configuration(dictionary = "dict.txt")
    }
}
