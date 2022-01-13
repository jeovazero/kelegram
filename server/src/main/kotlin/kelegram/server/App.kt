package kelegram.server

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.websocket.*
import io.ktor.sessions.*
import io.ktor.serialization.*
import kelegram.server.domain.*
import kelegram.server.persistence.UserPersistence
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import java.util.*
import kotlin.collections.HashMap
import kelegram.common.*
import kelegram.server.routes.inviteRoutes
import kelegram.server.routes.ownedRoomRoutes

data class UserSession(val id: String)

typealias RoomId = String
typealias UserId = String
val roomConnections = HashMap<RoomId,HashMap<UserId,DefaultWebSocketSession>>()

suspend fun setRoomsConnections(userId: String, conn: DefaultWebSocketSession) {
    val rooms = UserDomain.get(userId)?.roomsIds ?: listOf()
    rooms.forEach {
        if (roomConnections.get(it) == null) {
            val h = HashMap<UserId,DefaultWebSocketSession>()
            h.set(userId,conn)
            roomConnections.set(it,h)
        } else {
            roomConnections.get(it)?.set(userId,conn)
        }
    }
}

suspend fun removeRoomsConnections(userId: String) {
    val rooms = UserDomain.get(userId)?.roomsIds ?: listOf()
    rooms.forEach {
        roomConnections.get(it)?.remove(userId)
    }
}

val App = fun Application.() {
    install(WebSockets)
    install(ContentNegotiation) {
        json(Json {
            serializersModule = IdKotlinXSerializationModule
            prettyPrint = true
        })
    }
    install(Sessions) {
        cookie<UserSession>("user_session")
    }
    install(CORS) {
        allowCredentials = true
        allowSameOrigin = true
        header(HttpHeaders.ContentType)
        anyHost()
        header(HttpHeaders.AccessControlAllowOrigin)
        host("0.0.0.0:8080")
    }

    routing {
        ownedRoomRoutes()
        inviteRoutes()
        get("/") {
            call.respondText("What did you learn?")
        }

        get("/rooms") {
            val usession = call.sessions.get<UserSession>()
            if (usession != null) {
                val rooms = UserPersistence.getRooms(usession.id)
                call.respond(rooms)
            } else {
                call.respondText(text="What did you learn?",status = HttpStatusCode.NotFound)
            }
        }

        get("/rooms/{id}/messages") {
            val usession = call.sessions.get<UserSession>()
            val rid = call.parameters["id"]
            if (usession != null && rid != null) {
                // TODO: verify if the user is authorized in the room
                val msgs = RoomDomain.getMessages(rid)
                call.respond(msgs)
            } else {
                call.respondText(text="What did you learn?",status = HttpStatusCode.NotFound)
            }
        }

        get("/rooms/{id}/members") {
            val usession = call.sessions.get<UserSession>()
            val rid = call.parameters["id"]
            if (usession != null && rid != null) {
                // TODO: verify if the user is authorized in the room
                val msgs = RoomDomain.getMembers(rid)
                call.respond(msgs)
            } else {
                call.respondText(text="What did you learn?",status = HttpStatusCode.NotFound)
            }
        }

        get("/me") {
            val usession = call.sessions.get<UserSession>()
            if (usession != null) {
                val u = UserPersistence.get(usession.id)
                if (u != null) {
                    call.respond(u)
                } else {
                    call.respondText(text="What did you learn?",status = HttpStatusCode.NotFound)
                }
            } else {
                call.respondText(text="What did you learn?",status = HttpStatusCode.NotFound)
            }
        }

        post("/account") {
            try {
                val newUser = call.receive<NewUser>()
                val result = UserDomain.add(newUser)
                call.sessions.set(UserSession(id = result.id))
                call.respondText("Hello there ${result.id}")
            } catch (err: Exception) {
                println(err.printStackTrace())
                call.respondText("Hello there Eror")
            }
        }

        webSocket("/ember") {
            send("What did you learn?")
            val usession = call.sessions.get<UserSession>()
            if (usession == null) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT,
                    "Not authorized"))
            } else {
                val uid = usession.id
                setRoomsConnections(uid,this)

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            try {
                                val receivedText = frame.readText()
                                val msg =
                                    Json.decodeFromString<NewMessage>(receivedText)

                                println(receivedText)
                                val id = UUID.randomUUID().toString()
                                val msgWs = Message(
                                    msg.content,
                                    msg.fromUser,
                                    msg.toRoom,
                                    id
                                )
                                MessageDomain.add(msgWs)
                                val user = UserDomain.get(msg.fromUser)
                                if (user != null) {
                                    val msgToSend = MessageInfo(
                                        msg.content,
                                        UserInfo(user.nickname,user.id),
                                        msg.toRoom,
                                        id
                                    )
                                    roomConnections.get(msg.toRoom)?.forEach {
                                        it.value.send(Frame.Text(Json.encodeToString(
                                            msgToSend)))
                                    }
                                }
                            } catch (err: Exception) {
                                removeRoomsConnections(uid)

                                close(CloseReason(CloseReason.Codes.NORMAL,
                                    "Client didn't learned nothing"))
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8000, host = "0.0.0.0", module = App)
        .start(wait = true)
}