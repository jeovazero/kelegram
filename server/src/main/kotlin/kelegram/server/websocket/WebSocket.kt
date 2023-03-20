package kelegram.server.websocket

import kelegram.common.*
import kelegram.server.data.MessageDoc
import kelegram.server.domain.MessageDomain
import kelegram.server.domain.UserDomain
import kelegram.server.routes.SESSION_COOKIE
import kelegram.server.utils.Cursor
import kelegram.server.utils.Cursor.Companion.encode
import kelegram.server.utils.logger
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
import java.util.*

val now = { Clock.System.now().toLocalDateTime(TimeZone.UTC) }

fun webSocket() = websockets(
    "/kek" bind { ws: Websocket ->
        val nowTime = now()
        ws.send(
            WsMessage(
                Json.encodeToString(
                    MessageInfoCursor(
                        MessageInfo(
                            "What did you learn?",
                            UserInfo("hello", "***"),
                            "***",
                            "***",
                            nowTime.toString()
                        ), Cursor("***", nowTime).encode()
                    )
                )
            )
        )
        val req = ws.upgradeRequest
        val sessionId = req.cookie(SESSION_COOKIE)?.value
        println("SESSION $sessionId")
        val session = runBlocking {
            sessionId?.let { s -> UserDomain.getSession(s) }
        }
        if (session == null) {
            ws.close(
                WsStatus(REFUSE.code, "Not authorized")
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
                    val nowMsg = now()
                    val msgWs = MessageDoc(
                        msg.content,
                        msg.fromUser,
                        msg.toRoom,
                        id,
                        nowMsg.toString()
                    )
                    logger.debug { "Searching user by ${msg.fromUser}" }
                    val user = runBlocking {
                        MessageDomain.create(msgWs)
                        UserDomain.getById(msg.fromUser)
                    }
                    logger.debug { "Found ${user}" }
                    if (user != null) {
                        val msgToSend = MessageInfoCursor(
                            MessageInfo(
                                msg.content,
                                UserInfo(user.nickname, user.id),
                                msg.toRoom,
                                id,
                                msgWs.createdAt
                            ), Cursor(id, nowMsg).encode()
                        )
                        // TODO: verify permission to send a msg to a room
                        // naive solution, set all rooms again
                        // this case occurs when a user accepts an invitation and enters a new room
                        if (roomConnections.get(msg.toRoom)?.contains(user.id) != true) {
                            WSConnection.setRoom(msg.toRoom, user.id)
                        }

                        val msgText = Json.encodeToString(msgToSend)
                        WSConnection.sendToRoom(msg.toRoom, msgText)
                    } else {
                        logger.debug { "Something wrong with user: ${user}"}
                    }
                } catch (err: Exception) {
                    logger.debug { err.toString() }
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