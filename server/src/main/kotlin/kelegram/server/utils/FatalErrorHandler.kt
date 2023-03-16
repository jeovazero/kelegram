package kelegram.server.utils

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.filter.ServerFilters

val FatalErrorHandler = ServerFilters.CatchAll {
    logger.error { "Fatal Error: ${it.message}" }
    logger.debug { it.stackTraceToString() }

    Response(Status.INTERNAL_SERVER_ERROR).body("Fatal error")
}