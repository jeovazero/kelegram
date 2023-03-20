package kelegram.server.routes

import kelegram.common.NewUser
import kelegram.common.User
import kelegram.server.domain.UserDomain
import kelegram.server.utils.DecodeReq.decode
import kelegram.server.utils.ErrorResponse
import kelegram.server.utils.UserSession.getSession
import kelegram.server.utils.UserSession.getUserId
import kelegram.server.utils.logger
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.http4k.core.*
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
        req.getUserId()?.let {
            val user = UserDomain.getById(it) ?: return@let ErrorResponse.unauthorized
            logger.debug { "Session user: $user" }
            userLens(user, Response(OK))
        } ?: ErrorResponse.unauthorized
    }
}

@Serializable
data class CreatedUser(val userId: String)
val createdUserLens = Body.auto<CreatedUser>().toLens()

val account: HttpHandler = { req ->
    runBlocking {
        val newUserPayload =
            req.decode(newUserLens) ?: return@runBlocking ErrorResponse.decodingFailure

        // TODO: add id verification by identity provider
        val (newUser, session) = UserDomain.create(newUserPayload)

        val resp = Response(OK)
            .cookie(Cookie(SESSION_COOKIE, session.id, secure = true, httpOnly = true, sameSite = SameSite.None))
        createdUserLens.inject(CreatedUser(newUser.id), resp)
    }
}

val logout: HttpHandler = { req ->
    runBlocking {
        val session = req.getSession()

        session?.let { s -> UserDomain.removeSession(s.id) }

        Response(OK).body("OK").removeCookie(SESSION_COOKIE)
    }
}

fun accountRoutes(): RoutingHttpHandler =
    routes(
        "/me" bind Method.GET to me,
        "/logout" bind Method.POST to logout,
        "/account" bind Method.POST to account
    )
