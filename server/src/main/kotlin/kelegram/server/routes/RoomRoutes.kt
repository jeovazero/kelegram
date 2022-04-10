package kelegram.server.routes

import kelegram.common.MessageInfo
import kelegram.common.Room
import kelegram.common.UserInfo
import kelegram.server.data.UserData
import kelegram.server.domain.RoomDomain
import kelegram.server.domain.UserDomain
import kotlinx.coroutines.runBlocking
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.cookie
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

val roomsLens = Body.auto<List<Room>>().toLens()
val messagesInfoLens = Body.auto<List<MessageInfo>>().toLens()
val membersInfoLens = Body.auto<List<UserInfo>>().toLens()

val rooms: HttpHandler = { req ->
    runBlocking {
        val sessionId = req.cookie(SESSION_COOKIE)?.value
        val session = sessionId?.let { s -> UserDomain.getSession(s) }
        if (session != null) {
            val rooms = UserData.getRooms(session.userId)
            roomsLens(rooms, Response(OK))
        } else {
            Response(NOT_FOUND)
        }
    }
}

val roomMessages: HttpHandler = { req ->
    runBlocking {
        val sessionId = req.cookie(SESSION_COOKIE)?.value
        val session = sessionId?.let { s -> UserDomain.getSession(s) }
        val rid = req.path("id")
        if (session != null && rid != null) {
            // TODO: verify if the user is authorized in the room
            val msgs = RoomDomain.getMessages(rid)
            messagesInfoLens(msgs, Response(OK))
        } else {
            Response(NOT_FOUND)
        }
    }
}
val roomsMembers: HttpHandler = { req ->
    runBlocking {
        val sessionId = req.cookie(SESSION_COOKIE)?.value
        val session = sessionId?.let { s -> UserDomain.getSession(s) }
        val rid = req.path("id")
        if (session != null && rid != null) {
            // TODO: verify if the user is authorized in the room
            val members = RoomDomain.getMembers(rid)
            membersInfoLens(members, Response(OK))
        } else {
            Response(NOT_FOUND)
        }
    }
}

fun roomRoutes(): RoutingHttpHandler =
    "/rooms" bind routes(
        "/" bind Method.GET to rooms,
        "/{id:.*}/messages" bind Method.GET to roomMessages,
        "/{id:.*}/members" bind Method.GET to roomsMembers
)