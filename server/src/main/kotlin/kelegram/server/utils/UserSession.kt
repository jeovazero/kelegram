package kelegram.server.utils

import kelegram.server.domain.UserDomain
import kelegram.server.model.Session
import kelegram.server.routes.SESSION_COOKIE
import kelegram.server.utils.UserSession.getUserId
import org.http4k.core.Request
import org.http4k.core.cookie.cookie

object UserSession {
    suspend fun Request.getUserId(): String? = this.getSession()?.userId
    suspend fun Request.getSession(): Session? {
        val sessionId = this.cookie(SESSION_COOKIE)?.value
        return sessionId?.let { s -> UserDomain.getSession(s) }
    }
}