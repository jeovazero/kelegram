package kelegram.server.domain

import kelegram.common.NewRoom
import kelegram.common.Room
import kelegram.common.UserInfo
import kelegram.server.data.MessageData
import kelegram.server.data.MessageInfoDoc
import kelegram.server.data.RoomData
import kelegram.server.data.UserData
import kelegram.server.utils.Cursor
import java.util.*

sealed interface MessagesResult : GetMessagesResult, GetMembersResult

sealed interface GetMessagesResult {
    data class Ok(val messages: List<MessageInfoDoc>) : GetMessagesResult
}

sealed interface GetMembersResult {
    data class Ok(val members: List<UserInfo>) : GetMembersResult
}

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
        // TODO: atomic
        RoomData.addMember(roomId, memberId)
        UserData.addRoom(memberId, roomId)
    }

    suspend fun getMembers(requesterId: String, roomId: String): GetMembersResult {
        val result = RoomData.getMembers(roomId, requesterId)

        return if (result.isEmpty()) CommonResult.Forbidden
        else GetMembersResult.Ok(result)
    }

    suspend fun getMessages(requesterId: String, roomId: String, after: Cursor?, limit: Int): GetMessagesResult {
        RoomData.getRoomByMember(roomId, requesterId) ?: return CommonResult.Forbidden
        return GetMessagesResult.Ok(MessageData.getInfoByRoomId(roomId, after, limit))
    }
}
