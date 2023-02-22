package com.adisalagic.codenames.client.api

import com.google.gson.Gson
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets


abstract class BaseAPI(open val event: Int): Packetable {

    override fun writeAsPacket(): ByteArray {
        val string = Gson().toJson(this)
        val encoding = StandardCharsets.UTF_8
        var bytes = encoding.encode(string)
        var array: ByteArray
        array = bytes.array()
        if (bytes.remaining() > 0){
            array = array.copyOfRange(0, array.size - (array.size - bytes.remaining())) //getting rid of zero bytes
        }
        val size = array.size + Int.SIZE_BYTES
        return ByteBuffer
            .allocate(Int.SIZE_BYTES + size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(size)
            .putInt(event)
            .put(bytes)
            .array()
    }
}