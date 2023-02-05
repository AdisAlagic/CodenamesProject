package com.adisalagic.codenames.server

import com.google.gson.Gson
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets


open class BaseAPI(open val event: String?): Packetable {
    override fun toPaket(): ByteArray {
        val string = Gson().toJson(this)
        val encoding = StandardCharsets.UTF_8
        var bytes = encoding.encode(string)
        var array= bytes.array()
        val sizeArrayTemp = array.size
        if (bytes.remaining() > 0){
            array = array.copyOfRange(0, sizeArrayTemp - (sizeArrayTemp - bytes.remaining())) //getting rid of zero bytes
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