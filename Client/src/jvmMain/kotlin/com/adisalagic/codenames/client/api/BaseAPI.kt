package com.adisalagic.codenames.client.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

@Serializable
data class BaseAPI(
    val event: String,
) : Packetable {
    override fun writeAsPacket(): ByteArray {
        val string = Json.encodeToString(serializer(), this)
        val encoding = StandardCharsets.UTF_8
        val bytes = encoding.encode(string).array()
        val size = bytes.size
        return ByteBuffer
            .allocate(Int.SIZE_BYTES + size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(size)
            .put(bytes)
            .array()
    }
}