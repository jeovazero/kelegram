package kelegram.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class NewUser(val nickname: String)

@Serializable
data class User(
    val nickname: String,
    val id: String,
    val roomsIds: List<String> = listOf(),
    // @Transient val _id: Id<User> = newId(),
)

@Serializable
data class UserInfo(
    val nickname: String,
    val id: String
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
)

@Serializable
data class MessageInfo(
    val content: String,
    val user: UserInfo,
    val roomId: String,
    val id: String,
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