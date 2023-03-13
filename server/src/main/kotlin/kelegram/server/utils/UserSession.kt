package kelegram.server.utils

import kelegram.server.domain.UserDomain
import kelegram.server.routes.SESSION_COOKIE
import org.http4k.core.Request
import org.http4k.core.cookie.cookie

object UserSession {
    suspend fun get(req: Request): String? {
        val sessionId = req.cookie(SESSION_COOKIE)?.value
        val session = sessionId?.let { s -> UserDomain.getSession(s) }
        val uid = session?.userId

        return uid
    }
}