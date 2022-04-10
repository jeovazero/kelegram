package kelegram.server.domain

import InviteData
import kelegram.common.Invite
import java.util.*

object InviteDomain {
    val get = InviteData::get
    suspend fun create(roomId: String, ownerId: String): Invite {
        val id = UUID.randomUUID().toString()
        val invite = Invite(id,roomId,ownerId)
        InviteData.add(invite)
        return invite
    }
    val getInfo = InviteData::getInfo
}