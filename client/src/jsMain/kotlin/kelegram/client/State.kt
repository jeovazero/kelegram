package kelegram.client

import androidx.compose.runtime.MutableState
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.w3c.dom.WebSocket

@Serializable
data class User(val id: String, val nickname: String)

@Serializable
data class Room(
    val ownerId: String,
    val id: String,
    val name: String,
)

@Serializable
data class Message(
    val content: String,
    val userId: String,
    val roomId: String,
    val id: String,
)

enum class AppScreen {
    SignUp,
    RoomCreation,
    Main,
    Loading,
    Invite,
    InviteCreation
}

@Serializable
data class State(
    var screen: AppScreen,
    var user: User? = null,
    var rooms: List<Room>? = null,
    var selectedRoom: Room? = null,
    @Contextual var socket: WebSocket? = null,
)
typealias MState = MutableState<State>
