package kelegram.server.routes

import kelegram.common.NewRoom
import kelegram.server.WSConnection
import kelegram.server.domain.InviteDomain
import kelegram.server.domain.RoomDomain
import kelegram.server.domain.UserDomain
import kotlinx.coroutines.runBlocking
import org.http4k.core.*
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.cookie.cookie
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

val newRoomLens = Body.auto<NewRoom>().toLens()

val ownedRooms: HttpHandler = { req ->
    runBlocking {
        val sessionId = req.cookie(SESSION_COOKIE)?.value
        val session = sessionId?.let { s -> UserDomain.getSession(s) }
        val newRoom = newRoomLens.invoke(req)
        if (session != null) {
            val user = UserDomain.getById(session.userId)
            if (user != null) {
                val result = RoomDomain.create(newRoom, user.id)
                WSConnection.setRoom(result.id, user.id)
                Response(OK).body(result.id)
            } else {
                Response(NOT_FOUND).body("What did you learn?")
            }
        } else {
            Response(NOT_FOUND).body("What did you learn?")
        }
    }
}

val ownedRoomsInvites: HttpHandler = { req ->
    runBlocking {
        val sessionId = req.cookie(SESSION_COOKIE)?.value
        val session = sessionId?.let { s -> UserDomain.getSession(s) }
        val rid = req.path("id")
        val uid = session?.userId
        if (uid != null && rid != null) {
            val room = RoomDomain.get(rid,uid)
            if (room != null) {
                val invite = InviteDomain.create(rid,uid)
                Response(OK).body("/invites/${invite.id}")
            } else {
                Response(FORBIDDEN)
            }
        } else {
            Response(UNAUTHORIZED)
        }
    }
}

fun ownedRoomRoutes(): RoutingHttpHandler =
    routes(
        "/ownedrooms" bind Method.POST to ownedRooms,
        "/ownedrooms/{id:.*}/invites" bind Method.POST to ownedRoomsInvites
    )
