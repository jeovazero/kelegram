package kelegram.server.routes

import kelegram.common.InviteInfo
import kelegram.common.Room
import kelegram.server.domain.InviteDomain
import kelegram.server.domain.RoomDomain
import kelegram.server.domain.UserDomain
import kotlinx.coroutines.runBlocking
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.cookie
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

val inviteInfoLens = Body.auto<InviteInfo>().toLens()
val roomLens = Body.auto<Room>().toLens()

val invites: HttpHandler = { req ->
    runBlocking {
        val sessionId = req.cookie(SESSION_COOKIE)?.value
        val session = sessionId?.let { s -> UserDomain.getSession(s) }
        val uid = session?.userId
        val inviteId = req.path("id")
        if (uid != null && inviteId != null) {
            val invite = InviteDomain.getInfo(inviteId)
            print(invite)
            if (invite != null && invite.ownerId != uid) {
                inviteInfoLens(invite, Response(OK))
            } else {
                Response(BAD_REQUEST)
            }
        } else {
            Response(NOT_FOUND)
        }
    }
}

val createInvite: HttpHandler = { req ->
    runBlocking {
        val sessionId = req.cookie(SESSION_COOKIE)?.value
        val session = sessionId?.let { s -> UserDomain.getSession(s) }
        val uid = session?.userId
        val inviteId = req.path("id")
        if (uid != null && inviteId != null) {
            val invite = InviteDomain.get(inviteId)
            if (invite != null && invite.ownerId != uid) {
                RoomDomain.addMember(invite.roomId, uid)
                val room = RoomDomain.get(invite.roomId,invite.ownerId)
                if (room != null) {
                    roomLens(room, Response(OK))
                } else {
                    Response(BAD_REQUEST) // not sure
                }
            } else {
                Response(BAD_REQUEST) // not sure
            }
        } else {
            Response(NOT_FOUND)
        }
    }
}

fun inviteRoutes(): RoutingHttpHandler =
    "/invites/{id:.*}" bind routes(
        Method.GET to invites,
        Method.POST to createInvite
    )
