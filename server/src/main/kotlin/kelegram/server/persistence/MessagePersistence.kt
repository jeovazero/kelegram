package kelegram.server.persistence

import kelegram.common.Message
import kelegram.common.MessageInfo
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.aggregate

val messageCol = database.getCollection<Message>()

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