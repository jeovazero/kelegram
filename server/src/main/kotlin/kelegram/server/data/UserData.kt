package kelegram.server.data

import kelegram.common.IdentityProvider
import kelegram.common.Provider
import kelegram.common.Room
import kelegram.server.model.User
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.aggregate

val userCol = database.getCollection<User>()

object UserData {
    suspend fun add(userData: User) {
        userCol.insertOne(userData)
    }

    suspend fun getById(id: String): User? {
        return userCol.findOne(User::id eq id)
    }

    suspend fun getByIdFromProvider(idFromProvider: String): User? {
        return userCol.findOne(
            User::identityProvider / IdentityProvider::id eq idFromProvider
        )
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

