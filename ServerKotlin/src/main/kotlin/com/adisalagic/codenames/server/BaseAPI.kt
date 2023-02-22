package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import com.google.gson.Gson
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets


open class BaseAPI(open val event: Int): Packetable {
    override fun toPaket(): ByteArray {
        val logger = Logger.getLogger(this::class)
        val string = Gson().toJson(this)
        logger.debug("Message to adapt: $string")
        val encoding = StandardCharsets.UTF_8
        val bytes = encoding.encode(string)
        var array = bytes.array()
        val sizeArrayTemp = array.size
        if (bytes.remaining() > 0){
            array = array.copyOfRange(0, sizeArrayTemp - (sizeArrayTemp - bytes.remaining())) //getting rid of zero bytes
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