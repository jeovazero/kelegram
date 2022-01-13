package kelegram.server.domain

import InvitePersistence
import kelegram.common.Invite
import java.util.*

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