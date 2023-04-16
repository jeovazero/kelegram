package kelegram.server.data

import kelegram.common.UserInfo
import kelegram.server.utils.Cursor
import kelegram.server.utils.logger
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.conversions.Bson
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.aggregate

@Serializable
data class MessageDoc(
    val content: String,
    val userId: String,
    val roomId: String,
    val id: String,
    val createdAt: String,
    @Contextual val _id: Id<MessageDoc> = newId()
)

@Serializable
data class MessageInfoDoc(
    val content: String,
    val user: UserInfo,
    val roomId: String,
    val id: String,
    val createdAt: LocalDateTime,
    @Contextual val _id: Id<MessageDoc> = newId()
)

val messageCol = database.getCollection<MessageDoc>("message")

object MessageData {
    suspend fun add(msg: MessageDoc) {
        messageCol.insertOne(msg)
    }

    suspend fun getInfoByRoomId(roomId: String, afterCursor: Cursor?, limit: Int): List<MessageInfoDoc> {
        val roomFilter = MessageDoc::roomId eq roomId
        val afterFilter: Bson? =
            afterCursor?.let { and(MessageDoc::createdAt lt it.createdAt.toString()) }
        val filters = if (afterFilter != null) and(roomFilter, afterFilter) else roomFilter
        logger.debug {
            match(filters).toString()
        }
        return messageCol.aggregate<MessageInfoDoc>(
            match(filters),
            lookup(
                from = "user",
                localField = "userId",
                foreignField = "id",
                newAs = "user"
            ),
            unwind("user".projection),
            sort(orderBy(listOf(MessageDoc::createdAt, MessageDoc::_id), false)),
            limit(limit)
        ).toList()
    }
}