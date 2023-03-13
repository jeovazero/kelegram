package kelegram.server.utils

import org.http4k.core.HttpMessage
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.LensFailure


object DecodeReq {
    fun <T>HttpMessage.decode(lens: BiDiBodyLens<T>): T? {
        return try {
            lens.invoke(this)
        } catch (e: LensFailure) {
            logger.debug{ e.message }
            logger.debug{ String(this.body.payload.array()) }
            null
        }
    }
}