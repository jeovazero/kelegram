package kelegram.server.routes

import kelegram.common.MessageInfo
import kelegram.common.Room
import kelegram.common.UserInfo
import kelegram.server.data.UserData
import kelegram.server.domain.RoomDomain
import kelegram.server.domain.UserDomain
import kelegram.server.utils.ErrorResponse
import kelegram.server.utils.UserSession.getUserId
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
        req.getUserId() ?: return@runBlocking ErrorResponse.unauthorized
        val roomId = req.path("id") ?: return@runBlocking ErrorResponse.notFound

        // TODO: verify if the user is allowed in the room
        val messages = RoomDomain.getMessages(roomId)
        messagesInfoLens(messages, Response(OK))
    }
}
val roomsMembers: HttpHandler = { req ->
    runBlocking {
        val memberId = req.getUserId() ?: return@runBlocking ErrorResponse.unauthorized
        val roomId = req.path("id") ?: return@runBlocking ErrorResponse.notFound

        val members = RoomDomain.getMembers(roomId, memberId)
        if (members.isEmpty()) return@runBlocking ErrorResponse.forbidden
        membersInfoLens(members, Response(OK))
    }
}

fun roomRoutes(): RoutingHttpHandler =
    "/rooms" bind routes(
        "/" bind Method.GET to rooms,
        "/{id:.*}/messages" bind Method.GET to roomMessages,
        "/{id:.*}/members" bind Method.GET to roomsMembers
    )