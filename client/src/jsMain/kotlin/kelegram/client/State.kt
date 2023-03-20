package kelegram.client

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kelegram.common.MessageInfoCursor
import kelegram.common.Room
import kelegram.common.User
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.w3c.dom.WebSocket

enum class AppScreen {
    SignUp,
    Main,
    Loading,
    Login,
    None
}

@Serializable
data class State(
    var screen: AppScreen,
    var routeParams: Map<String, String>? = null,
    var user: User? = null,
    var rooms: List<Room>? = null,
    var selectedRoom: Room? = null,
    @Contextual var messages: SnapshotStateList<MessageInfoCursor> = mutableStateListOf(),
    @Contextual var socket: WebSocket? = null,
)

typealias MState = MutableState<State>
