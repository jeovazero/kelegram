package kelegram.server.data

import com.mongodb.client.model.IndexOptions
import kelegram.common.Invite
import kelegram.common.Room
import kelegram.common.User
import kelegram.server.model.Session
import org.litote.kmongo.ascendingIndex
import org.litote.kmongo.descendingIndex

suspend fun ensureIndexes() {
    messageCol.ensureIndex(
        descendingIndex(MessageDoc::createdAt, MessageDoc::id),
        indexOptions = IndexOptions().unique(true)
    )
    roomCol.ensureIndex(
        ascendingIndex(Room::id),
        indexOptions = IndexOptions().unique(true)
    )
    userCol.ensureIndex(
        ascendingIndex(User::id),
        indexOptions = IndexOptions().unique(true)
    )
    inviteCol.ensureIndex(
        ascendingIndex(Invite::id),
        indexOptions = IndexOptions().unique(true)
    )
    sessionCol.ensureIndex(
        ascendingIndex(Session::id),
        indexOptions = IndexOptions().unique(true)
    )
}