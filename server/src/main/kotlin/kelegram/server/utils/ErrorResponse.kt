package kelegram.server.utils

import org.http4k.core.Response
import org.http4k.core.Status

object ErrorResponse {
    val decodingFailure = Response(Status.BAD_REQUEST).body("Decoding failure")
}