package kelegram.server.domain

import kelegram.common.NewRoom
import kelegram.common.Room
import kelegram.server.persistence.MessagePersistence
import kelegram.server.persistence.RoomPersistence
import kelegram.server.persistence.UserPersistence
import java.util.*

object RoomDomain {
    suspend fun add(newRoom: NewRoom, ownerId: String): Room {
        val id = UUID.randomUUID().toString()
        val room = Room(newRoom.name, ownerId, id, listOf(ownerId))
        RoomPersistence.add(room)
        UserPersistence.addRoom(ownerId, room.id)
        return room
    }
    val get = RoomPersistence::get
    suspend fun addMember(roomId: String, memberId: String) {
        RoomPersistence.addMember(roomId, memberId)
        UserPersistence.addRoom(memberId, roomId)
    }
    val getMembers = RoomPersistence::getMembers
    val getMessages = MessagePersistence::getInfoByRoomId
}