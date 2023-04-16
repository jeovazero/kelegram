package kelegram.server.data

import kelegram.common.Room
import kelegram.common.UserInfo
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.aggregate

val roomCol = database.getCollection<Room>()


object RoomData {
    suspend fun add(roomData: Room) {
        roomCol.insertOne(roomData)
    }

    suspend fun get(id: String, userId: String? = null): Room? {
        if (userId != null) {
            return roomCol.findOne(and(Room::id eq id, Room::ownerId eq userId))
        }
        return roomCol.findOne(Room::id eq id)
    }

    suspend fun getRoomByMember(id: String, userId: String): Room? =
        roomCol.findOne(and(Room::id eq id, Room::membersIds contains userId))


    suspend fun addMember(roomId: String, memberId: String) {
        roomCol.updateOne(Room::id eq roomId,
            push(Room::membersIds, memberId)
        )
    }

    suspend fun getMembers(roomId: String, memberId: String): List<UserInfo> {
        return roomCol.aggregate<UserInfo>(
            // TODO: review match
            match(and(Room::id eq roomId, Room::membersIds contains memberId)),
            lookup(from = "user",
                localField = "membersIds",
                foreignField = "id",
                newAs = "members"),
            unwind("members".projection),
            replaceRoot("members".projection)
        ).toList()
    }
}