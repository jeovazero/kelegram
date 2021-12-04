import androidx.compose.runtime.*
import kelegram.client.*
import kelegram.client.State
import kelegram.client.pages.LoadingPage
import kelegram.client.pages.MainPage
import kelegram.client.pages.SignupPage
import kelegram.client.ui.AppWrapper
import kelegram.client.ui.ButtonStyle
import kelegram.client.ui.SpacingStyle
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.WebSocket

/*
val mainScope = MainScope()


@Composable
fun RoomCreation(state: MState) {
    Stack {
        Text("Create a room")
        Input(type = InputType.Text,attrs = {
            name("roomname")
        })
        Button(attrs = {
            onClick {
                mainScope.launch {
                    createRoom()
                    state.value = state.value.copy(screen = AppScreen.Main)
                }
            }
        }) {
            Text("Send")
        }
    }
}

@Composable
fun InviteCreation(state: MState) {
    val invite = remember { mutableStateOf("") }
    Stack {
        Text("Create an invite")
        Button(attrs = {
            onClick {
                mainScope.launch {
                    val room = state.value.selectedRoom
                    if (room != null) {
                        val value = createInvite(room.id)
                        if (value != null) {
                            invite.value = value
                        }
                    }
                }
            }
        }) {
            Text("Send")
        }
        Div {
            if (invite.value.isNotEmpty()) {
                Text("Invite: ${invite.value}")
            }
        }
        Button(attrs = {
            onClick {
                state.value = state.value.copy(screen = AppScreen.Main)
            }
        }) { Text("Home") }
    }
}


@Composable
fun Rooms(state: MState) {
    Stack {
        Button (attrs = {
            onClick {
                state.value = state.value.copy(screen = AppScreen.RoomCreation)
            }
        }) {
            Text("new room")
        }
        state.value.rooms?.forEach { room ->
            Div (attrs = {
                if (state.value.selectedRoom?.id == room.id) {
                    classes(AppStylesheet.roomSelected)
                }
                classes(AppStylesheet.room)
                onClick {
                    mainScope.launch {
                        getMessages(room.id)
                        state.value = state.value.copy(selectedRoom = room)
                    }
                }
            }) { Text(room.name) }
        }
    }
}



@Composable
fun Chat(state: MState, messages: List<String>) {
    Stack {
        Button (attrs = {
            onClick {
                state.value = state.value.copy(screen = AppScreen.InviteCreation)
            }
        }) { Text("Invite") }
        Div { messages.forEach { Text(it) } }
        Inline {
            Input(type= InputType.Text, attrs = { name("msg")})
            Button(attrs = {
                onClick {
                    val input = document.querySelector("input[name=msg]") as HTMLInputElement
                    val m = MessageWS(
                        fromUser = state.value.user?.id ?: "",
                        toRoom = state.value.selectedRoom?.id ?: "",
                        content = input.value
                    )
                    state.value.socket?.send(Json.encodeToString(m))
                }
            }) { Text("Send") }
        }
    }
}


@Composable
fun Invite(state: MState, inviteState: MutableState<String>) {
    val roomState = remember { mutableStateOf<Room?>(null) }
    LaunchedEffect(inviteState.value) {
        if(inviteState.value.isNotEmpty()) {
            val room = validateInvite(inviteState.value)
            if (room != null) {
                roomState.value = room
            }
        } else {
            roomState.value = null
        }
    }
    Stack {
        val room = roomState.value
        if (room != null) {
            Title("Invite accepted to the room, ${room.name}")
        } else {
            Title("No invite here")
        }

    }
}
*/
fun main() {
    renderComposable(rootElementId = "root") {
        val state = remember { mutableStateOf(State(screen=AppScreen.Loading)) }
        val user = state.value.user
        console.log(state.value.toString())

        LaunchedEffect(user) {
            if (user != null) {
                val nextState = state.value.copy(screen = AppScreen.Main)
                if (state.value.socket == null) {
                    val socket = WebSocket(url = "ws://localhost:8000/ember")
                    nextState.socket = socket
                }
                state.value = nextState
            }
        }

        Style(ResetStylesheet)
        Style(KelegramStylesheet)
        Style(ButtonStyle)
        Style(SpacingStyle)
        AppWrapper {
            when(state.value.screen) {
                AppScreen.Loading -> LoadingPage(state)
                AppScreen.SignUp -> SignupPage(state)
                AppScreen.Main -> MainPage(state)
                //AppScreen.RoomCreation -> RoomCreation(state)
                //AppScreen.InviteCreation -> InviteCreation(state)
                //AppScreen.Invite -> Invite(state,inviteState)
            }
            //Invite(owner="Akasha")
        }
    }
}