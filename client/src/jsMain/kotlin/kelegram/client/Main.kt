import androidx.compose.runtime.*
import kelegram.client.*
import kelegram.client.State
import kelegram.client.ui.AppWrapper
import kelegram.client.ui.Stack
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.hover
import org.jetbrains.compose.web.css.selectors.plus
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLInputElement
import org.w3c.fetch.*
import kotlin.js.json

object AppStylesheet : StyleSheet() {
    init {
        "body" style {
            margin(0.px)
        }
    }
    val room by style {
        padding(8.px)
        self + hover() style {
            backgroundColor(Color.azure)
            color(Color.mediumvioletred)
            cursor("pointer")
            border{
                style(LineStyle.Dashed)
                width(1.px)
                color(Color.white)
            }
        }
    }
    val roomSelected by style {
        border{
            style(LineStyle.Solid)
            width(1.px)
            color(Color.white)
        }
    }
}

@Serializable
data class MessageWS(val fromUser: String, val toRoom: String, val content: String)

val base = "http://localhost:8000"

suspend fun request(path: String = "/",method: String = "GET", body: dynamic = null): Response {
    return window.fetch(base + path, object : RequestInit {
        override var method: String? = method
        override var body: dynamic = body
        override var headers: dynamic = json(
            "Accept" to "application/json",
            "Content-Type" to "application/json"
        )
        override var credentials: RequestCredentials? = RequestCredentials.Companion.INCLUDE
    }).await()
}

suspend fun me(): User? {
    val r = request(
        path = "/me"
    )
    console.log("R", r.ok)
    if (r.ok) {
        val w = r.text().await()
        console.log(w)
        return Json.decodeFromString(w)
    }
    return null
}

suspend fun getRooms(): List<Room>? {
    val r = request(
        path = "/rooms"
    )
    if (r.ok) {
        val w = r.text().await()
        return Json.decodeFromString(w)
    }
    return null
}

suspend fun getChannels(): List<Room>? {
    val r = request(
        path = "/channels"
    )
    if (r.ok) {
        val w = r.text().await()
        return Json.decodeFromString(w)
    }
    return null
}

suspend fun getMessages(id: String): List<Message>? {
    val r = request(
        path = "/rooms/$id/messages",
    )
    if (r.ok) {
        val w = r.text().await()
        return Json.decodeFromString(w)
    }
    return null
}

suspend fun createAccount() {
    val nick = document.querySelector("input[name=nickname]") as HTMLInputElement
    val r = request(
        path = "/account",
        method = "POST",
        body=JSON.stringify(json("nickname" to nick.value))
    )
    console.log("R", r.ok)
    val w = r.text().await()
    console.log(w)
}

suspend fun createRoom(): String? {
    val el = document.querySelector("input[name=roomname]") as HTMLInputElement
    val r = request(
        path = "/rooms",
        method = "POST",
        body=JSON.stringify(json("name" to el.value))
    )
    console.log("R", r.ok)
    if (r.ok) {
        return r.text().await()
    }
    return null
}

suspend fun createInvite(roomId: String): String? {
    val r = request(
        path = "/rooms/$roomId/invites",
        method = "POST"
    )
    console.log("R", r.ok)
    if (r.ok) {
        return r.text().await()
    }
    return null
}

suspend fun validateInvite(inviteId: String): Room? {
    val r = request(
        path = "/invites/$inviteId",
        method = "POST"
    )
    console.log("R", r.ok)
    if (r.ok) {
        val w = r.text().await()
        return Json.decodeFromString(w)
    }
    return null
}

val mainScope = MainScope()

@Composable
fun Title(content: String) {
    H3 {
        Text(content)
    }
}

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
fun Inline(content: @Composable () -> Unit) {
    Div (attrs = {
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
        }
    }) {
        content()
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

suspend fun setMe(mstate: MState) {
    val user = me()
    if (user != null) {
        mstate.value = mstate.value.copy(user = user)
        console.log("DIFF")
    } else {
        mstate.value =
            mstate.value.copy(user = null, screen = AppScreen.SignUp)
    }
}

@Composable
fun Loading(mstate: MState) {
    mainScope.launch {
        setMe(mstate)
    }
    Text("Loading...")
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
fun Members() {
    Stack {
        Text("You")
        Text("Member A")
        Text("Member B")
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

@Composable
fun App(state: MState) {
    val msg = remember { mutableStateListOf<String>() }
    LaunchedEffect(state.value.socket) {
        state.value.socket?.apply {
            onmessage = {
                console.log(it.data)
                msg.add(it.data as String)
            }
            onopen = { print("Open:" + it.type) }
            onclose = { print("close:" + it.type) }
        }
    }
    Inline {
        Rooms(state)
        if (state.value.selectedRoom != null) {
            Chat(state,msg)
            Members()
        }
    }
}


fun main() {
    console.log("Render")
    renderComposable(rootElementId = "root") {
        val state = remember { mutableStateOf(State(screen=AppScreen.SignUp)) }
        /*
        val inviteState = remember { mutableStateOf("") }
        val user = state.value.user
        console.log(state.value.toString())

        val hash = window.location.hash
        console.log(hash)


        LaunchedEffect(hash) {
            val paths = hash.split("/")
            if (paths.size >= 3) {
                console.log(paths)
                if (paths[0] == "#" && paths[1] == "invites") {
                    inviteState.value = paths[2]
                }
            }
        }

        LaunchedEffect(inviteState.value) {
            if (inviteState.value.isNotEmpty()) {
                state.value = state.value.copy(screen = AppScreen.Invite)
            }
        }

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
        LaunchedEffect(state.value.screen) {
            if (state.value.screen === AppScreen.Main) {
                val r = getChannels()
                val nextState = state.value.copy()
                if (r != null) {
                    nextState.rooms = r
                    state.value = nextState
                }
            }
        }

         */
        // Style(AppStylesheet)
        Style(ResetStylesheet)
        Style(KelegramStylesheet)
        AppWrapper {
            when(state.value.screen) {
                //AppScreen.Loading -> Loading(state)
                AppScreen.SignUp -> Signup(state)
                //AppScreen.Main -> App(state)
                //AppScreen.RoomCreation -> RoomCreation(state)
                //AppScreen.InviteCreation -> InviteCreation(state)
                //AppScreen.Invite -> Invite(state,inviteState)
            }
            //Invite(owner="Akasha")
        }
    }
}