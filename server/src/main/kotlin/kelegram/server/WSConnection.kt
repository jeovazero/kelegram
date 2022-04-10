package kelegram.server

import kelegram.server.domain.UserDomain
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import java.util.HashMap
import java.util.HashSet


typealias RoomId = String
typealias UserId = String

val roomConnections = HashMap<RoomId, HashSet<UserId>>()
val userConnections = HashMap<UserId, Websocket>()

object WSConnection {
    suspend fun setRooms(conn: Websocket, userId: UserId) {
        val rooms = UserDomain.getById(userId)?.roomsIds ?: listOf()
        setUser(conn, userId)
        rooms.forEach { setRoom(it, userId) }
    }
    fun setUser(conn: Websocket, userId: UserId) {
        userConnections.set(userId, conn)
    }
    fun setRoom(roomId: RoomId, userId: String)  {
        val userHashSet = roomConnections.get(roomId)
        if (userHashSet == null) {
            val h = HashSet<UserId>()
            h.add(userId)
            roomConnections.set(roomId, h)
        } else {
            userHashSet.add(userId)
        }
    }
    suspend fun removeAll(userId: String) {
        val rooms = UserDomain.getById(userId)?.roomsIds ?: listOf()
        rooms.forEach {
            roomConnections.get(it)?.remove(userId)
        }
        userConnections.remove(userId)
    }
    fun sendToRoom(roomId: RoomId, msg: String) {
        roomConnections.get(roomId)?.forEach {
            userConnections.get(it)?.send(WsMessage(msg))
        }
    }
}
