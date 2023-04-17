package kelegram.server.domain

import kelegram.common.Invite
import kelegram.common.Room
import kelegram.server.data.InviteData
import java.util.*

sealed interface InviteResult: AcceptInviteResult, CreateInviteResult

sealed interface AcceptInviteResult {
    data class Ok(val room: Room) : AcceptInviteResult
}

sealed interface CreateInviteResult {
    data class Ok(val invite: Invite): CreateInviteResult
}

object InviteDomain {
    val get = InviteData::get
    val remove = InviteData::remove
    suspend fun create(roomId: String, ownerId: String): CreateInviteResult {
        val room = RoomDomain.get(roomId, ownerId) ?: return CommonResult.NotFound
        val id = UUID.randomUUID().toString()
        val invite = Invite(id,room.id,ownerId)
        InviteData.add(invite)
        return CreateInviteResult.Ok(invite)
    }
    val getInfo = InviteData::getInfo
    suspend fun acceptInvite(inviteId: String, requesterId: String): AcceptInviteResult {
        val invite = get(inviteId) ?: return CommonResult.NotFound

        if (invite.ownerId == requesterId) return CommonResult.Inconsistent

        val room =
            RoomDomain.get(invite.roomId, invite.ownerId) ?: return CommonResult.Forbidden

        RoomDomain.addMember(invite.roomId, requesterId)
        remove(invite.id)

        return AcceptInviteResult.Ok(room)
    }
}