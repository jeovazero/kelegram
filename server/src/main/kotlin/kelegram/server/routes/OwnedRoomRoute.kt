package kelegram.server.routes

import kelegram.common.NewRoom
import kelegram.server.domain.*
import kelegram.server.websocket.WSConnection
import kelegram.server.utils.DecodeReq.decode
import kelegram.server.utils.ErrorResponse
import kelegram.server.utils.UserSession.getUserId
import kelegram.server.utils.handleCommonResult
import kotlinx.coroutines.runBlocking
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

val newRoomLens = Body.auto<NewRoom>().toLens()

val ownedRooms: HttpHandler = { req ->
    runBlocking {
        val requesterId = req.getUserId() ?: return@runBlocking ErrorResponse.unauthorized
        val newRoom = req.decode(newRoomLens) ?: return@runBlocking ErrorResponse.decodingFailure
        if (newRoom.name.isEmpty()) return@runBlocking ErrorResponse.unprocessableContent
        val user = UserDomain.getById(requesterId) ?: return@runBlocking ErrorResponse.notFound

        val result = RoomDomain.create(newRoom, user.id)

        WSConnection.setRoom(result.id, user.id)
        Response(OK).body(result.id)
    }
}

val ownedRoomsInvites: HttpHandler = { req ->
    runBlocking {
        val requesterId = req.getUserId() ?: return@runBlocking ErrorResponse.unauthorized
        val roomId = req.path("id")  ?: return@runBlocking ErrorResponse.notFound

        val result = InviteDomain.create(roomId, requesterId)

        when(result) {
            is CreateInviteResult.Ok -> Response(OK).body("/invites/${result.invite.id}")
            is CommonResult -> handleCommonResult(result)
        }
    }
}

fun ownedRoomRoutes(): RoutingHttpHandler =
    routes(
        "/ownedrooms" bind Method.POST to ownedRooms,
        "/ownedrooms/{id:.*}/invites" bind Method.POST to ownedRoomsInvites
    )
