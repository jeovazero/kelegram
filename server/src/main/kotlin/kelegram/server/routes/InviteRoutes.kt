package kelegram.server.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kelegram.server.UserSession
import kelegram.server.domain.InviteDomain
import kelegram.server.domain.RoomDomain

fun Route.inviteRoutes() {
    get("/invites/{id}") {
        val uid = call.sessions.get<UserSession>()?.id
        val inviteId = call.parameters["id"]
        if (uid != null && inviteId != null) {
            val invite = InviteDomain.getInfo(inviteId)
            print(invite)
            if (invite != null && invite.ownerId != uid) {
                call.respond(invite)
            } else {
                call.respondText(text="What did you learn?",status = HttpStatusCode.InternalServerError)
            }
        } else {
            call.respondText(text="What did you learn?",status = HttpStatusCode.NotFound)
        }
    }

    post("/invites/{id}") {
        val uid = call.sessions.get<UserSession>()?.id
        val inviteId = call.parameters["id"]
        if (uid != null && inviteId != null) {
            val invite = InviteDomain.get(inviteId)
            if (invite != null && invite.ownerId != uid) {
                RoomDomain.addMember(invite.roomId, uid)
                val room = RoomDomain.get(invite.roomId,invite.ownerId)
                if (room != null) {
                    call.respond(room)
                } else {
                    call.respondText(text="What did you learn?",status = HttpStatusCode.InternalServerError)
                }
            }
        } else {
            call.respondText(text="What did you learn?",status = HttpStatusCode.NotFound)
        }
    }
}