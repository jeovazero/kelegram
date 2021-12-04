package kelegram.server.persistence

import kelegram.common.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val client = KMongo.createClient().coroutine //use coroutine extension
val database = client.getDatabase("test") //normal java driver usage
val userCol = database.getCollection<User>()
val roomCol = database.getCollection<Room>()
val inviteCol = database.getCollection<Invite>()
val messageCol = database.getCollection<Message>()

object UserPersistence {
    suspend fun add(userData: User) {
        userCol.insertOne(userData)
    }

    suspend fun get(id: String): User? {
        return userCol.findOne(User::id eq id)
    }

    /* db.roomUser.aggregate([
        { $match: { userId: "d7ee23ce-8288-4ba6-ac1d-9a3221d7b2bb" } },
        { $lookup: { from: "room", localField: "roomId", foreignField: "id", as: "room" } },
        { $unwind: "$room" },
        { $replaceRoot: {newRoot: "$room"}}
       ])
     */
    suspend fun getRooms(userId: String): List<Room> {
        return userCol.aggregate<Room>(
            match(User::id eq userId),
            project(User::roomsIds),
            lookup(from = "room",
                localField = "roomsIds",
                foreignField = "id",
                newAs = "rooms"),
            unwind("rooms".projection),
            replaceRoot("rooms".projection)
        ).toList()
    }

    suspend fun addRoom(userId: String, roomId: String) {
        userCol.updateOne(User::id eq userId,
            push(User::roomsIds, roomId)
        )
    }
}

object RoomPersistence {
    suspend fun add(roomData: Room) {
        roomCol.insertOne(roomData)
    }

    suspend fun get(id: String, userId: String? = null): Room? {
        if (userId != null) {
            return roomCol.findOne(and(Room::id eq id, Room::ownerId eq userId))
        }
        return roomCol.findOne(Room::id eq id)
    }

    suspend fun addMember(roomId: String, memberId: String) {
        roomCol.updateOne(Room::id eq roomId,
            push(Room::membersIds, memberId)
        )
    }

    suspend fun getMembers(roomId: String): List<UserInfo> {
        return roomCol.aggregate<UserInfo>(
            match(Room::id eq roomId),
            lookup(from = "user",
                localField = "membersIds",
                foreignField = "id",
                newAs = "members"),
            unwind("members".projection),
            replaceRoot("members".projection)
        ).toList()
    }
}

object MessagePersistence {
    suspend fun add(msg: Message) {
        messageCol.insertOne(msg)
    }

    suspend fun getByRoomId(roomId: String): List<Message> {
        return messageCol.find(Message::roomId eq roomId).toList()
    }

    suspend fun getInfoByRoomId(roomId: String): List<MessageInfo> {
        return messageCol.aggregate<MessageInfo>(
            match(Message::roomId eq roomId),
            lookup(from = "user",
                localField = "userId",
                foreignField = "id",
                newAs = "user"),
            unwind("user".projection)
        ).toList()
    }
}

object InvitePersistence {
    suspend fun add(inviteData: Invite) {
        inviteCol.insertOne(inviteData)
    }

    suspend fun get(inviteId: String): Invite? {
        return inviteCol.findOne(Invite::id eq inviteId)
    }

    suspend fun getInfo(inviteId: String): InviteInfo? {
        val result = inviteCol.aggregate<InviteInfo>(
            match(Invite::id eq inviteId),
            lookup(from = "room",
                localField = "roomId",
                foreignField = "id",
                newAs = "room"),
            unwind("room".projection),
            lookup(from = "user",
                localField = "ownerId",
                foreignField = "id",
                newAs = "user"),
            unwind("user".projection),
            project(
                InviteInfo::ownerName from "user.nickname".projection,
                InviteInfo::ownerId from "user.id".projection,
                InviteInfo::roomName from "room.name".projection,
                InviteInfo::id from "id".projection
            )
        ).toList()
        if (result.isEmpty()) {
            return null
        }
        return result.first()
    }
}
