package kelegram.server.utils

import io.github.oshai.KotlinLogging
import org.http4k.core.HttpTransaction
import org.http4k.filter.ResponseFilters


val logger = KotlinLogging.logger {}

val LogHandler =
    ResponseFilters.ReportHttpTransaction { tx: HttpTransaction ->
        logger.info{ "${tx.request.uri} returned ${tx.response.status} and took ${tx.duration.toMillis()}ms" }
    }
