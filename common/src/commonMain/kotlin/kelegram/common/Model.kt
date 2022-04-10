package kelegram.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
enum class Provider {
    @SerialName("GITHUB") Github,
    @SerialName("FAKE") Fake
}

@Serializable
data class IdentityProvider(val id: String, val provider: Provider)

@Serializable
data class NewUser(
    val nickname: String,
    val identityProvider: IdentityProvider,
    val avatarUrl: String? = null,
)

@Serializable
data class User(
    val id: String,
    val identityProvider: IdentityProvider,
    val nickname: String,
    val avatarUrl: String?,
    val roomsIds: List<String> = listOf(),
    // @Transient val _id: Id<User> = newId(),
)

@Serializable
data class UserInfo(
    val nickname: String,
    val id: String,
)

@Serializable
data class NewRoom(val name: String)

@Serializable
data class Room(
    val name: String,
    val ownerId: String,
    val id: String,
    val membersIds: List<String> = listOf(),
    // @Transient val _id: Id<Room> = newId(),
)

@Serializable
data class NewMessage(
    val fromUser: String, val toRoom: String, val content: String,
)

@Serializable
data class Message(
    val content: String,
    val userId: String,
    val roomId: String,
    val id: String,
    val createdAt: String
)

@Serializable
data class MessageInfo(
    val content: String,
    val user: UserInfo,
    val roomId: String,
    val id: String,
    val createdAt: String
)

@Serializable
data class Invite(val id: String, val roomId: String, val ownerId: String)

@Serializable
data class InviteInfo(
    val id: String,
    val roomName: String,
    val ownerName: String,
    @Transient val ownerId: String = "",
)