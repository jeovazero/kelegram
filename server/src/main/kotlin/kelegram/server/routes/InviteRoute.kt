package kelegram.server.routes

import kelegram.common.InviteInfo
import kelegram.common.Room
import kelegram.server.domain.InviteDomain
import kelegram.server.domain.RoomDomain
import kelegram.server.utils.ErrorResponse
import kelegram.server.utils.UserSession.getUserId
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

val inviteInfoLens = Body.auto<InviteInfo>().toLens()
val roomLens = Body.auto<Room>().toLens()

val invites: HttpHandler = { req ->
    runBlocking {
        val inviteId = req.path("id") ?: return@runBlocking ErrorResponse.notFound
        val requesterId = req.getUserId() ?: return@runBlocking ErrorResponse.unauthorized

        val invite = InviteDomain.getInfo(inviteId) ?: return@runBlocking ErrorResponse.notFound
        logger.debug { invite }
        if (invite.ownerId == requesterId) return@runBlocking ErrorResponse.unprocessableEntity

        inviteInfoLens(invite, Response(OK))
    }
}

val acceptInvite: HttpHandler = { req ->
    runBlocking {
        val inviteId = req.path("id") ?: return@runBlocking ErrorResponse.notFound
        val requesterId = req.getUserId() ?: return@runBlocking ErrorResponse.unauthorized

        val invite = InviteDomain.get(inviteId) ?: return@runBlocking ErrorResponse.notFound

        if (invite.ownerId == requesterId) return@runBlocking ErrorResponse.unprocessableEntity

        val room =
            RoomDomain.get(invite.roomId, invite.ownerId) ?: return@runBlocking ErrorResponse.unprocessableEntity

        RoomDomain.addMember(invite.roomId, requesterId)
        // TODO: remove invite
        roomLens(room, Response(OK))
    }
}

fun inviteRoutes(): RoutingHttpHandler =
    "/invites/{id:.*}" bind routes(
        Method.GET to invites,
        Method.POST to acceptInvite
    )
