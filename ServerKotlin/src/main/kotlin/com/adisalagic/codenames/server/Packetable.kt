package com.adisalagic.codenames.server

interface Packetable {
    fun toPaket(): ByteArray
}