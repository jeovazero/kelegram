package kelegram.server.websocket

import kelegram.common.Message
import kelegram.common.MessageInfo
import kelegram.common.NewMessage
import kelegram.common.UserInfo
import kelegram.server.domain.MessageDomain
import kelegram.server.domain.UserDomain
import kelegram.server.routes.SESSION_COOKIE
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.cookie.cookie
import org.http4k.routing.bind
import org.http4k.routing.websockets
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.http4k.websocket.WsStatus
import org.http4k.websocket.WsStatus.Companion.ABNORMAL_CLOSE
import org.http4k.websocket.WsStatus.Companion.REFUSE
import java.time.LocalDateTime
import java.util.*

fun webSocket() = websockets(
    "/kek" bind { ws: Websocket ->
        ws.send(WsMessage(Json.encodeToString(MessageInfo(
            "What did you learn?",
            UserInfo("hello", "***"),
            "***",
            "***",
            LocalDateTime.now().toString()
        ))))
        val req = ws.upgradeRequest
        val sessionId = req.cookie(SESSION_COOKIE)?.value
        println("SESSION $sessionId")
        val session = runBlocking {
            sessionId?.let { s -> UserDomain.getSession(s) }
        }
        if (session == null) {
            ws.close(
                WsStatus(REFUSE.code,"Not authorized")
            )
        } else {
            val uid = session.userId
            runBlocking {
                WSConnection.setRooms(ws, uid)
                println("\u001B[32m[WS]: CONNECTING $uid")
                println("\u001B[0m")
            }
            ws.onMessage {
                try {
                    val receivedText = it.body.toString()
                    println("RECEIVED: $receivedText")
                    val msg =
                        Json.decodeFromString<NewMessage>(
                            receivedText
                        )
                    val id = UUID.randomUUID().toString()
                    val msgWs = Message(
                        msg.content,
                        msg.fromUser,
                        msg.toRoom,
                        id,
                        LocalDateTime.now().toString()
                    )
                    val user = runBlocking {
                        MessageDomain.create(msgWs)
                        UserDomain.getById(msg.fromUser)
                    }
                    if (user != null) {
                        val msgToSend = MessageInfo(
                            msg.content,
                            UserInfo(user.nickname, user.id),
                            msg.toRoom,
                            id,
                            msgWs.createdAt
                        )
                        // TODO: verify permission to send a msg to a room
                        // naive solution, set all rooms again
                        // this case occurs when a user accepts an invitation and enters a new room
                        if (roomConnections.get(msg.toRoom)?.contains(user.id) != true) {
                            WSConnection.setRoom(msg.toRoom, user.id)
                        }

                        val msgText = Json.encodeToString(msgToSend)
                        WSConnection.sendToRoom(msg.toRoom, msgText)
                    }
                } catch (err: Exception) {
                    runBlocking {
                        WSConnection.removeAll(uid)
                    }
                    ws.close(
                        WsStatus(
                            ABNORMAL_CLOSE.code,
                            "Client didn't learned nothing"
                        )
                    )
                }
                ws.onClose { status ->
                    runBlocking {
                        WSConnection.removeAll(uid)
                        println("CLOSE ${status.description} $uid")
                    }
                }
            }
        }
    }
)