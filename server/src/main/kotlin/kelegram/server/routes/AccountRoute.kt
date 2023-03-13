package kelegram.server.routes

import kelegram.common.NewUser
import kelegram.common.User
import kelegram.server.domain.UserDomain
import kelegram.server.domain.UserDomain.getById
import kelegram.server.utils.DecodeReq.decode
import kelegram.server.utils.ErrorResponse
import kelegram.server.utils.logger
import kotlinx.coroutines.runBlocking
import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.removeCookie
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

const val SESSION_COOKIE = "session"
val userLens = Body.auto<User>().toLens()
val newUserLens = Body.auto<NewUser>().toLens()

val me: HttpHandler = { req ->
    runBlocking {
        val sessionId = req.cookie(SESSION_COOKIE)?.value
        val session = sessionId?.let { s -> UserDomain.getSession(s) }
        if (session != null) {
            val u = getById(session.userId)
            println("Session user: $u")
            if (u != null) {
                userLens(u, Response(OK))
            } else {
                Response(FORBIDDEN).body("FORBIDDEN")
            }
        } else {
            Response(FORBIDDEN).body("FORBIDDEN")
        }
    }
}

val account: HttpHandler = { req ->
    runBlocking {
        try {
            val newUserPayload =
                req.decode(newUserLens) ?: return@runBlocking ErrorResponse.decodingFailure
            val newUser = UserDomain.create(newUserPayload)
            val session = UserDomain.createSession(newUser)
            Response(OK)
                .cookie(Cookie(SESSION_COOKIE, session.id, secure = true, httpOnly = true, sameSite = SameSite.None))
                .body("Hello there ${newUser.id}")
        } catch (err: Exception) {
            logger.error { "Fatal Error: ${err.message}" }
            Response(INTERNAL_SERVER_ERROR).body("Fatal error on create account")
        }
    }
}

val logout: HttpHandler = { req ->
    runBlocking {
        try {
            val sessionId = req.cookie(SESSION_COOKIE)?.value
            val session = sessionId?.let { s -> UserDomain.getSession(s) }
            session?.let { s -> UserDomain.removeSession(s.id) }
            Response(OK).removeCookie(SESSION_COOKIE)
        } catch (err: Exception) {
            println(err.printStackTrace())
            Response(INTERNAL_SERVER_ERROR)
        }
    }
}

fun accountRoutes(): RoutingHttpHandler =
    routes(
        "/me" bind Method.GET to me,
        "/logout" bind Method.GET to logout,
        "/account" bind Method.POST to account
    )
