import kelegram.common.Invite
import kelegram.common.InviteInfo
import kelegram.server.persistence.database
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.aggregate

val inviteCol = database.getCollection<Invite>()

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