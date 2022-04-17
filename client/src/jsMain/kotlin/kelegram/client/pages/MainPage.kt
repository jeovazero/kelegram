package kelegram.client.pages

import androidx.compose.runtime.*
import kelegram.client.*
import kelegram.client.modals.AcceptInviteModal
import kelegram.client.modals.CreateInviteModal
import kelegram.client.tokens.Token
import kelegram.client.ui.*
import kelegram.common.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement

object MainStylesheet : StyleSheet() {
    val wrapper by style {
        marginTop(2.cssRem)
    }
    val box by style { // container is a class
        padding(1.5.cssRem)
        backgroundColor(Token.pallete.primaryText)
        alignSelf(AlignSelf.Center)
        display(DisplayStyle.Flex)
        textAlign("left")
        justifyContent(JustifyContent.Center)
        borderRadius(0.px, 0.px, 16.px, 16.px)
    }
    val headerBox by style {
        padding(1.cssRem, 2.cssRem)
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.SpaceBetween)
        alignItems(AlignItems.Center)
        property("box-shadow", Token.shadow.header)
        backgroundColor(Token.pallete.primaryText)
        textAlign("center")
        width(100.percent)
        boxSizing("border-box")
        property("z-index", "1")
        borderRadius(16.px, 16.px, 0.px, 0.px)
    }
    val list by style {
        width(128.px)
        padding(1.cssRem)
        paddingTop(0.px)
    }
    val listHeader by style {
        alignItems(AlignItems.Center)
        paddingTop(1.cssRem)
        paddingBottom(1.cssRem)
        child(self, type("div")) style {
            marginRight(8.px)
        }
    }
    val listBody by style {
        paddingLeft(1.cssRem)
    }
    val listItem by style {
        color(AppCSSVariables.neutralMedium.value())
        marginBottom(0.5.cssRem)
        marginTop(0.5.cssRem)
        cursor("pointer")
        self + hover style {
            color(AppCSSVariables.neutralDark.value())
        }
    }
    val memberItem by style {
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        self + before style {
            display(DisplayStyle.Block)
            property("content", "''")
            width(8.px)
            height(8.px)
            backgroundColor(AppCSSVariables.tertiary.value())
            borderRadius(50.percent)
            marginRight(8.px)
        }
    }
    val currentMember by style {
        fontWeight("bold")
    }
    val roomItem by style {
        border {
            color(Color.transparent)
            style(LineStyle.Solid)
        }
        paddingLeft(8.px)
        borderWidth(0.px, 0.px, 0.px, 4.px)
    }
    val roomSeleted by style {
        fontWeight("bold")
        property("border-color", AppCSSVariables.primary.value())
    }
    val title by style {
        fontSize(1.5.cssRem)
        color(AppCSSVariables.neutralDark.value())
    }
    val user by style {
        fontSize(1.5.cssRem)
        paddingTop(1.5.cssRem)
        paddingBottom(1.5.cssRem)
        color(AppCSSVariables.neutralMedium.value())
    }
    val chat by style {
        width(512.px)
        padding(1.cssRem)
        paddingTop(0.px)
    }
    val messages by style {
        backgroundColor(AppCSSVariables.neutralLight.value())
        width(100.percent)
        height(512.px)
        boxSizing("border-box")
        padding(1.5.cssRem)
        borderRadius(16.px)
        marginBottom(1.cssRem)
        overflow("auto")
    }
    val input by style {
        width(100.percent)
        padding(1.cssRem)
        fontSize(1.125.cssRem)
        borderRadius(12.px)
        backgroundColor(AppCSSVariables.neutralLight.value())
        border {
            width(0.px)
            style(LineStyle.None)
        }
        marginRight(1.cssRem)
    }
}

object BaloonStyle : StyleSheet() {
    val wrapper by style {
        width(100.percent)
        marginBottom(0.5.cssRem)
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        alignItems(AlignItems.FlexStart)
    }
    val isOwn by style {
        alignItems(AlignItems.FlexEnd)
    }
    val username by style {
        marginBottom(4.px)
        marginTop(6.px)
    }
    val textWrapper by style {
        maxWidth(56.percent)
        borderRadius(8.px)
        padding(0.75.cssRem)
        color(Color.black)
        backgroundColor(hsl(184, 82, 85))
        property("word-break","break-all")
    }
}

@Composable
fun MessageBaloon(message: String, nickname: String, isOwn: Boolean = false) {
    Div(attrs = {
        classes(BaloonStyle.wrapper)
        if (isOwn) {
            classes(BaloonStyle.isOwn)
        }
    }) {
        if (!isOwn) {
            Div(attrs = {
                classes(BaloonStyle.username)
            }) {
                Span { Text(nickname) }
            }
        }
        Div(attrs = {
            classes(BaloonStyle.textWrapper)
        }) {
            Span { Text(message) }
        }
    }
}


enum class ModalSelect {
    CreateRoom,
    AcceptInvite,
    CreateInvite,
    None
}

typealias ModalSelectState = MutableState<ModalSelect>

@Composable
fun Rooms(
    modalSelectState: ModalSelectState,
    rooms: List<Room>,
    selectedRoom: Room?,
    onClick: (Room) -> Unit,
) {
    Div(attrs = { classes(MainStylesheet.list) }) {
        Stack {
            Inline(className = MainStylesheet.listHeader) {
                AddButton(onClick = {
                    modalSelectState.value = ModalSelect.CreateRoom
                })
                H3(attrs = { classes(MainStylesheet.title) }) {
                    Text("Rooms")
                }
            }
            Stack(className = MainStylesheet.listBody) {
                rooms.forEach { room ->
                    Div(attrs = {
                        classes(MainStylesheet.listItem,
                            MainStylesheet.roomItem)
                        if (room.id == selectedRoom?.id) {
                            classes(MainStylesheet.roomSeleted)
                        }
                        this.onClick {
                            onClick(room)
                        }
                    }) {
                        Span { Text(room.name) }
                    }
                }
            }
        }
    }
}

@Composable
fun Members(modalSelectState: ModalSelectState, currentUser: User, members: List<UserInfo>) {
    Inline(className = MainStylesheet.listHeader) {
        AddButton(onClick = {
            modalSelectState.value = ModalSelect.CreateInvite
        })
        H3(attrs = { classes(MainStylesheet.title) }) {
            Text("Members")
        }
    }
    Stack(className = MainStylesheet.listBody) {
        members.forEach { member ->
            Div (attrs = {
                classes(MainStylesheet.listItem,MainStylesheet.memberItem)
                if (member.id == currentUser.id) {
                    classes(MainStylesheet.currentMember)
                }
            }) {
                Span{ Text(member.nickname)}
            }
        }
    }
}

@Composable
fun Header(username: String?) {
    Div(attrs = { classes(MainStylesheet.headerBox) }) {
        Logo()
        if (username != null) {
            H3(attrs = { classes(MainStylesheet.user) }) { Text(username) }
        }
    }
}

@Composable
fun Chat(name: String, messages: List<MessageInfo>, userId: String?, state: MState) {

        Stack {
            Div(attrs = { classes(MainStylesheet.listHeader) }) {
                H3(attrs = { classes(MainStylesheet.title) }) {
                    Text(name)
                }
            }
            Div(attrs = {
                classes(MainStylesheet.messages)
            }) {
                Stack {
                    messages.forEach { message ->
                        MessageBaloon(message.content,
                            message.user.nickname, message.user.id == userId)
                    }
                }
            }
            Inline {
                Input(type = InputType.Text, attrs = {
                    placeholder("Say hello...")
                    classes(MainStylesheet.input)
                    name("newmessage")
                })
                Button(
                    onClick = {
                        val input =
                            document.querySelector("input[name=newmessage]") as HTMLInputElement
                        val m = NewMessage(
                            fromUser = state.value.user?.id ?: "",
                            toRoom = state.value.selectedRoom?.id
                                ?: "",
                            content = input.value
                        )
                        state.value.socket?.send(Json.encodeToString(
                            m))
                    }
                ) { Text("Send") }
            }
        }
}

@Composable
fun MainPage(state: MState) {
    Style(MainStylesheet)
    Style(BaloonStyle)
    val modalSelectState = remember { mutableStateOf(ModalSelect.None) }
    val msg = remember { mutableStateListOf<MessageInfo>() }
    val membersState = remember { mutableStateListOf<UserInfo>() }
    val scope = rememberCoroutineScope()

    val user = state.value.user
    val rooms = state.value.rooms ?: listOf()
    val selectedRoom = state.value.selectedRoom
    val inviteId = state.value.routeParams?.get("inviteId")
    console.log("$inviteId")
    LaunchedEffect(user) {
        if(user == null) {
            dispatch(state, Action.DefineMe)
        }
    }
    LaunchedEffect(Unit) {
        dispatch(state, Action.GetRooms)
    }
    val inviteState = remember { mutableStateOf<InviteInfo?>(null) }
    val inviteLink = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(inviteId) {
        if(inviteId != null) {
            val result = getInvite(inviteId)
            console.log("INMVITTT $inviteId")
            if (result != null) {
                inviteState.value = result
                modalSelectState.value = ModalSelect.AcceptInvite
            } else {
                console.log("Somenthing wrong, I can feel it")
                dispatch(state, Action.Redirect("/app"))
            }
        }
    }
    LaunchedEffect(state.value.socket) {
        state.value.socket?.apply {
            onmessage = {
                console.log(it.data)
                msg.add(Json.decodeFromString(it.data as String))
            }
            onopen = { print("Open:" + it.type) }
            onclose = { print("close:" + it.type) }
        }
    }
    Stack(className = MainStylesheet.wrapper) {
        Header(user?.nickname)
        Div(attrs = { classes(MainStylesheet.box) }) {
            Inline {
                Rooms(modalSelectState,
                    rooms,
                    selectedRoom,
                    onClick = { room: Room ->
                        scope.launch {
                            console.log("GET messages ${room.id}")
                            val list = getMessages(room.id)
                            if (list != null) {
                                msg.clear()
                                msg.addAll(list)
                            }
                            state.value = state.value.copy(selectedRoom = room)
                            console.log("set rooms")
                        }
                        scope.launch {
                            console.log("GET members ${room.id}")
                            val list = getMembers(room.id)
                            if (list != null) {
                                membersState.clear()
                                membersState.addAll(list)
                            }
                            console.log("set members")
                        }
                    })
                Div(attrs = { classes(MainStylesheet.chat) }) {
                    if (selectedRoom != null) {
                        Chat(selectedRoom.name,
                            messages = msg,
                            userId = user?.id,
                            state)
                    } else {
                        Div { Text("Select a room") }
                    }
                }
                Div(attrs = { classes(MainStylesheet.list) }) {
                    if (state.value.selectedRoom != null && user != null) {
                        Members(modalSelectState, user, membersState)
                    }
                }
            }
        }
    }
    when (modalSelectState.value) {
        ModalSelect.CreateRoom -> NewRoomModal(onCancel = {
            modalSelectState.value = ModalSelect.None
        }, onConfirm = { roomName ->
            scope.launch {
                dispatch(state, Action.CreateRoom(roomName))
                modalSelectState.value = ModalSelect.None
            }
        })
        ModalSelect.CreateInvite -> CreateInviteModal(onCancel = {
            modalSelectState.value = ModalSelect.None
        }, onConfirm = {
            if (selectedRoom?.id != null) {
                scope.launch {
                    val result = createInvite(selectedRoom.id)
                    inviteLink.value = result
                }
            }
        }, inviteLink = inviteLink.value)
        ModalSelect.AcceptInvite -> {
            val invite = inviteState.value
            if (invite != null) {
                AcceptInviteModal(invite, onCancel = {
                    modalSelectState.value = ModalSelect.None
                    window.location.replace("")
                }, onConfirm = {
                    scope.launch { validateInvite(invite.id) }
                })
            }
        }
    }
}
