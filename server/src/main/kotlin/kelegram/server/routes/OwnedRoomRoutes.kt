package kelegram.server.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kelegram.common.NewRoom
import kelegram.server.UserSession
import kelegram.server.domain.InviteDomain
import kelegram.server.domain.RoomDomain
import kelegram.server.domain.UserDomain

fun Route.ownedRoomRoutes() {
    post("/ownedrooms") {
        val usession = call.sessions.get<UserSession>()
        val newRoom = call.receive<NewRoom>()
        if (usession != null) {
            val user = UserDomain.get(usession.id)
            if (user != null) {
                val result = RoomDomain.add(newRoom, user.id)
                call.respond(result.id)
            } else {
                call.respondText(text="What did you learn?",status = HttpStatusCode.NotFound)
            }
        } else {
            call.respondText(text="What did you learn?",status = HttpStatusCode.NotFound)
        }
    }

    post("/ownedrooms/{id}/invites") {
        val usession = call.sessions.get<UserSession>()
        val rid = call.parameters["id"]
        val uid = usession?.id
        if (uid != null && rid != null) {
            val room = RoomDomain.get(rid,uid)
            if (room != null) {
                val invite = InviteDomain.add(rid,uid)
                call.respondText(text="/invites/${invite.id}")
            } else {
                call.respondText(text="What did you learn?",status = HttpStatusCode.Forbidden)
            }
        } else {
            call.respondText(text="What did you learn?",status = HttpStatusCode.Unauthorized)
        }
    }
}