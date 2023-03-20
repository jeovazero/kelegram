package kelegram.server.routes

import kelegram.common.*
import kelegram.server.data.MessageInfoDoc
import kelegram.server.data.UserData
import kelegram.server.domain.*
import kelegram.server.utils.Cursor
import kelegram.server.utils.Cursor.Companion.encode
import kelegram.server.utils.ErrorResponse
import kelegram.server.utils.UserSession.getUserId
import kelegram.server.utils.handleCommonResult
import kelegram.server.utils.logger
import kotlinx.coroutines.runBlocking
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

val roomsLens = Body.auto<List<Room>>().toLens()
val messagesInfoLens = Body.auto<MessageInfoPage>().toLens()
val membersInfoLens = Body.auto<List<UserInfo>>().toLens()

val rooms: HttpHandler = { req ->
    runBlocking {
        val requesterId = req.getUserId() ?: return@runBlocking ErrorResponse.unauthorized

        val rooms = UserData.getRooms(requesterId)

        roomsLens(rooms, Response(OK))
    }
}

fun MessageInfoDoc.withCursor(): MessageInfoCursor {
    val cursor = Cursor(this._id.toString(), this.createdAt)
    val message = MessageInfo(this.content, this.user, this.roomId, this.id, this.createdAt.toString())

    return MessageInfoCursor(message, cursor.encode())
}

val roomMessages: HttpHandler = { req ->
    runBlocking {
        val requesterId = req.getUserId() ?: return@runBlocking ErrorResponse.unauthorized
        val roomId = req.path("id") ?: return@runBlocking ErrorResponse.notFound
        val afterCursor = req.query("after")?.let { Cursor.Companion.decode(it) }
        logger.debug { afterCursor.toString() }
        val limit = req.query("limit")?.toIntOrNull() ?: 20

        val result = RoomDomain.getMessages(requesterId, roomId, afterCursor, limit)

        when (result) {
            is GetMessagesResult.Ok -> {
                val messages = result.messages.map{ it.withCursor() }
                val hasNext = messages.size > limit
                messagesInfoLens(MessageInfoPage(messages.take(limit), hasNext), Response(OK))
            }
            is CommonResult -> handleCommonResult(result)
        }
    }
}
val roomsMembers: HttpHandler = { req ->
    runBlocking {
        val requesterId = req.getUserId() ?: return@runBlocking ErrorResponse.unauthorized
        val roomId = req.path("id") ?: return@runBlocking ErrorResponse.notFound

        val result = RoomDomain.getMembers(requesterId, roomId)

        when (result) {
            is GetMembersResult.Ok -> membersInfoLens(result.members, Response(OK))
            is CommonResult -> handleCommonResult(result)
        }
    }
}

fun roomRoutes(): RoutingHttpHandler = "/rooms" bind routes(
    "/" bind Method.GET to rooms,
    "/{id:.*}/messages" bind Method.GET to roomMessages,
    "/{id:.*}/members" bind Method.GET to roomsMembers
)