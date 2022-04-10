package kelegram.server.routes

import kelegram.common.NewUser
import kelegram.common.User
import kelegram.server.domain.UserDomain
import kelegram.server.domain.UserDomain.getById
import kotlinx.coroutines.runBlocking
import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
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
        println("Session: $session")
        if (session != null) {
            val u = getById(session.userId)
            println("Session user: $u")
            if (u != null) {
                userLens(u, Response(OK))
            } else {
                Response(NOT_FOUND).body("NOT_FOUND")
            }
        } else {
            Response(NOT_FOUND).body("NOT_FOUND")
        }
    }
}

val account: HttpHandler = { req ->
    runBlocking {
        try {
            val newUserPayload = newUserLens.invoke(req)
            val newUser = UserDomain.create(newUserPayload)
            val session = UserDomain.createSession(newUser)
            Response(OK).cookie(Cookie(SESSION_COOKIE, session.id)).body("Hello there ${newUser.id}")
        } catch (err: Exception) {
            println(err.printStackTrace())
            Response(BAD_REQUEST).body("ERROR on create account")
        }
    }
}

fun accountRoutes(): RoutingHttpHandler =
    routes(
        "/me" bind Method.GET to me,
        "/account" bind Method.POST to account
    )
