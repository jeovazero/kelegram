package kelegram.server.domain

import kelegram.common.NewRoom
import kelegram.common.Room
import kelegram.server.data.MessageData
import kelegram.server.data.RoomData
import kelegram.server.data.UserData
import java.util.*

object RoomDomain {
    suspend fun create(newRoom: NewRoom, ownerId: String): Room {
        val id = UUID.randomUUID().toString()
        val room = Room(newRoom.name, ownerId, id, listOf(ownerId))
        RoomData.add(room)
        UserData.addRoom(ownerId, room.id)
        return room
    }
    val get = RoomData::get
    suspend fun addMember(roomId: String, memberId: String) {
        RoomData.addMember(roomId, memberId)
        UserData.addRoom(memberId, roomId)
    }
    val getMembers = RoomData::getMembers
    val getMessages = MessageData::getInfoByRoomId
}
