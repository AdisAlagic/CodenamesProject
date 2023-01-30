package com.adisalagic.codenames.server

import com.google.gson.Gson
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets


open class BaseAPI(val event: String?): Packetable {
    override fun toPaket(): ByteArray {
        val string = Gson().toJson(this)
        val encoding = StandardCharsets.UTF_8
        var bytes = encoding.encode(string)
        var array: ByteArray
        array = bytes.array()
        if (bytes.remaining() > 0){
            array = array.copyOfRange(0, array.size - (array.size - bytes.remaining())) //getting rid of zero bytes
        }
        val size = array.size
        return ByteBuffer
            .allocate(Int.SIZE_BYTES + size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(size)
            .put(bytes)
            .array()
    }
}