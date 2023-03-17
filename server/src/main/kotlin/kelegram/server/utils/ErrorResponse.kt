package kelegram.server.utils

import org.http4k.core.Response
import org.http4k.core.Status

object ErrorResponse {
    val decodingFailure = Response(Status.BAD_REQUEST).body("Decoding failure")
    val notFound = Response(Status.NOT_FOUND).body("Resource not found")
    val forbidden = Response(Status.FORBIDDEN).body("Forbidden")
    val unauthorized = Response(Status.UNAUTHORIZED).body("Unauthorized")
    val unprocessableEntity = Response(Status.UNPROCESSABLE_ENTITY).body("Semantically incorrect parameters")
}