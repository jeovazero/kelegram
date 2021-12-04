package kelegram.server.domain

import kelegram.server.persistence.InvitePersistence
import kelegram.server.persistence.MessagePersistence
import kelegram.server.persistence.RoomPersistence
import kelegram.server.persistence.UserPersistence
import java.util.*
import kelegram.common.*

object UserDomain {
    suspend fun add(newUser: NewUser): User {
        val id = UUID.randomUUID().toString()
        val user = User(newUser.nickname, id)
        UserPersistence.add(user)
        return user
    }

    val get = UserPersistence::get

    val getParticipatedRooms = UserPersistence::getRooms
}

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

object MessageDomain {
    val add = MessagePersistence::add
}

object InviteDomain {
    val get = InvitePersistence::get
    suspend fun add(roomId: String, ownerId: String): Invite {
        val id = UUID.randomUUID().toString()
        val invite = Invite(id,roomId,ownerId)
        InvitePersistence.add(invite)
        return invite
    }
    val getInfo = InvitePersistence::getInfo
}