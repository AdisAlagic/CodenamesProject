package com.adisalagic.codenames.client.api

interface Packetable {
    fun writeAsPacket(): ByteArray
}