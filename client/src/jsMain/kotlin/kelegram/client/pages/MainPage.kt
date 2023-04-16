package kelegram.client.pages

import androidx.compose.runtime.*
import kelegram.client.modals.AcceptInviteModal
import kelegram.client.modals.CreateInviteModal
import kelegram.client.modals.DisconnectedModal
import kelegram.client.service.Json
import kelegram.client.service.Service.createInvite
import kelegram.client.service.Service.getInvite
import kelegram.client.service.Service.getMembers
import kelegram.client.service.Service.getMessages
import kelegram.client.service.Service.validateInvite
import kelegram.client.state.*
import kelegram.client.tokens.Token
import kelegram.client.ui.*
import kelegram.common.*
import kotlinx.browser.document
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement

object MainStylesheet : StyleSheet() {
    val wrapper by style {
        marginTop(16.px)
        property("height", "calc(100% - 32px)")
        height(CSSCalcValue(100.percent.minus(32.px)))
    }
    val box by style { // container is a class
        padding(0.5.cssRem, 1.cssRem)
        backgroundColor(Token.pallete.primaryText)
        alignSelf(AlignSelf.Center)
        display(DisplayStyle.Flex)
        textAlign("left")
        justifyContent(JustifyContent.Center)
        borderRadius(0.px, 0.px, 16.px, 16.px)
        flex(1)
        boxSizing("border-box")
        property("height", "calc(100% - 56px - 2rem)")
    }
    val headerBox by style {
        padding(1.cssRem, 1.5.cssRem)
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
        padding(0.px, 0.5.cssRem)
    }
    val listHeader by style {
        alignItems(AlignItems.Center)
        paddingTop(0.5.cssRem)
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
        color(AppCSSVariables.neutralMedium.value())
    }
    val chat by style {
        width(512.px)
        padding(0.5.cssRem)
        paddingTop(0.px)
        paddingBottom(16.px)
        height(100.percent)
        boxSizing("border-box")
    }
    val chatWrapper by style {
        height(100.percent)
    }
    val messages by style {
        backgroundColor(AppCSSVariables.neutralLight.value())
        width(100.percent)
        boxSizing("border-box")
        padding(1.5.cssRem)
        borderRadius(16.px)
        marginBottom(1.cssRem)
        overflow("auto")
        minHeight(240.px)
        height(100.percent)
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
    val headerUserWrapper by style {
        alignItems(AlignItems.Center)
        maxWidth(300.px)
    }
    val loadMore by style {

        textAlign("center")
        textDecorationLine("underline")
        cursor("pointer")
    }
    val truncateText by style {
        property("text-overflow", "ellipsis")
        overflow("hidden")
        property("white-space", "no-wrap")
    }
    val logout by style {
        fontSize(12.px)
        textAlign("right")
        color(AppCSSVariables.highlight.value())
        self + hover style {
            textDecorationLine("underline")
            cursor("pointer")
        }
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
        property("word-break", "break-all")
    }
}

@Composable
fun MessageBaloon(message: String, nickname: String?, isOwn: Boolean) {
    Div(attrs = {
        classes(BaloonStyle.wrapper)
        if (isOwn) {
            classes(BaloonStyle.isOwn)
        }
    }) {
        if (nickname != null) {
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
    CreateRoom, AcceptInvite, CreateInvite, Disconnect, None
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
                        classes(
                            MainStylesheet.listItem, MainStylesheet.roomItem
                        )
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
            Div(attrs = {
                classes(MainStylesheet.listItem, MainStylesheet.memberItem)
                if (member.id == currentUser.id) {
                    classes(MainStylesheet.currentMember)
                }
            }) {
                Span { Text(member.nickname) }
            }
        }
    }
}

@Composable
fun Header(mstate: MState, username: String?, avatar: String?) {
    val scope = rememberCoroutineScope()
    Div(attrs = { classes(MainStylesheet.headerBox) }) {
        Logo()
        if (username != null) {
            Stack {
                Inline(className = MainStylesheet.headerUserWrapper) {
                    if (avatar != null) {
                        Img(avatar, "avatar", attrs = {
                            style {
                                width(32.px)
                                height(32.px)
                                marginRight(8.px)
                                borderRadius(50.percent)
                            }
                        })
                    }
                    H3(attrs = { classes(MainStylesheet.user, MainStylesheet.truncateText) }) {
                        Text(username)
                    }
                }
                A(attrs = {
                    classes(MainStylesheet.logout)
                    onClick {
                        scope.launch {
                            dispatch(mstate, Action.Logout)
                        }
                    }
                }) { Span { Text("Logout") } }
            }
        }
    }
}

data class MessageBalloonInfo(val isOwn: Boolean, val author: String?, val content: String)

fun List<MessageInfoCursor>.toMessageBalloonInfo(userId: String) =
    this.mapIndexed { i, info ->
        val user = info.message.user
        val isOwn = user.id == userId
        val showAuthor = if (i != 0) {
            !isOwn && this[i - 1].message.user.id != user.id
        } else {
            !isOwn
        }
        val author = if (showAuthor) user.nickname else null
        MessageBalloonInfo(isOwn, author, info.message.content)
    }

@Composable
fun Chat(
    name: String,
    messages: List<MessageInfoCursor>,
    hasNextPage: Boolean,
    userId: String?,
    state: MState,
    lastMessageSource: LoadMessageAction,
    loadNextPage: (cursor: String) -> Unit
) {
    val messagesWrapperScrollable = remember { mutableStateOf<HTMLDivElement?>(null) }
    val ref = messagesWrapperScrollable.value
    LaunchedEffect(messages.size, ref) {
        if (ref != null) {
            when(lastMessageSource) {
                LoadMessageAction.LoadMore -> ref.scrollTop = 0.0
                else -> ref.scrollTop = ref.scrollHeight.toDouble()
            }
        }
    }
    Stack(className = MainStylesheet.chatWrapper) {
        Div(attrs = { classes(MainStylesheet.listHeader) }) {
            H3(attrs = { classes(MainStylesheet.title) }) {
                Text(name)
            }
        }
        Div(attrs = {
            classes(MainStylesheet.messages)
            ref {
                messagesWrapperScrollable.value = it
                onDispose { messagesWrapperScrollable.value = null }
            }
        }) {
            Stack {
                if (userId != null) {
                    if (hasNextPage) {
                        Box(className = MainStylesheet.loadMore) {
                            A(attrs = {
                                onClick {
                                    messages.first().let { loadNextPage(it.cursor) }
                                }
                            }) { Text("Load more") }
                        }
                    }
                    messages.toMessageBalloonInfo(userId).forEach { message ->
                        MessageBaloon(
                            message.content, message.author, message.isOwn
                        )
                    }
                }
            }
        }
        Inline {
            Input(type = InputType.Text, attrs = {
                placeholder("Say hello...")
                classes(MainStylesheet.input)
                name("newmessage")
            })
            Button(onClick = {
                val input = document.querySelector("input[name=newmessage]") as HTMLInputElement
                val m = NewMessage(
                    fromUser = state.value.user?.id ?: "",
                    toRoom = state.value.selectedRoom?.id ?: "",
                    content = input.value
                )
                state.value.socket?.send(
                    Json.encodeToString(
                        m
                    )
                )
                input.value = ""
            }) { Text("Send") }
        }
    }
}

enum class LoadMessageAction {
    LoadMore,
    Send,
    Initial
}
typealias RoomId = String

@Composable
fun MainPage(state: MState) {
    Style(MainStylesheet)
    Style(BaloonStyle)
    val loading = remember { mutableStateOf(true) }
    val modalSelectState = remember { mutableStateOf(ModalSelect.None) }
    val messagesState = remember { mutableStateListOf<MessageInfoCursor>() }
    val hasNextPage = remember { mutableStateOf(false) }
    val lastMessageSource = remember { mutableStateOf(LoadMessageAction.Initial) }
    val membersState = remember { mutableStateListOf<UserInfo>() }
    val scope = rememberCoroutineScope()

    val user = state.value.user
    val rooms = state.value.rooms ?: listOf()
    val selectedRoom = state.value.selectedRoom
    val inviteId = state.value.routeParams?.get("inviteId")
    LaunchedEffect(user) {
        if (user == null) {
            dispatch(state, Action.DefineMe)
        } else {
            loading.value = false
        }
    }
    LaunchedEffect(Unit) {
        dispatch(state, Action.GetRooms)
    }
    val inviteState = remember { mutableStateOf<InviteInfo?>(null) }
    val inviteLink = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(inviteId) {
        if (inviteId != null) {
            val result = getInvite(inviteId)
            if (result != null) {
                inviteState.value = result
                modalSelectState.value = ModalSelect.AcceptInvite
            } else {
                console.error("Somenthing wrong, I can feel it")
                dispatch(state, Action.Redirect("/app"))
            }
        }
    }
    LaunchedEffect(state.value.socket) {
        state.value.socket?.apply {
            onmessage = {
                lastMessageSource.value = LoadMessageAction.Send
                messagesState.add(Json.decodeFromString(it.data as String))
            }
            onopen = { }
            onclose = {
                modalSelectState.value = ModalSelect.Disconnect
                Unit
            }
        }
    }
    if (loading.value) {
        LoadingPage()
    } else {
        Stack(className = MainStylesheet.wrapper) {
            Header(state, user?.nickname, user?.avatarUrl)
            Div(attrs = { classes(MainStylesheet.box) }) {
                Inline {
                    Rooms(modalSelectState, rooms, selectedRoom, onClick = { room: Room ->
                        scope.launch {
                            val page = getMessages(room.id)
                            lastMessageSource.value = LoadMessageAction.Initial
                            if (page != null) {
                                messagesState.clear()
                                messagesState.addAll(page.messages.reversed())
                                hasNextPage.value = page.hasNext
                            } else {
                                messagesState.clear()
                            }
                            state.value = state.value.copy(selectedRoom = room)
                        }
                        scope.launch {
                            val list = getMembers(room.id)
                            if (list != null) {
                                membersState.clear()
                                membersState.addAll(list)
                            }
                        }
                    })
                    Div(attrs = { classes(MainStylesheet.chat) }) {
                        if (selectedRoom != null) {
                            Chat(
                                selectedRoom.name,
                                messages = messagesState,
                                userId = user?.id,
                                hasNextPage = hasNextPage.value,
                                state = state,
                                lastMessageSource = lastMessageSource.value,
                                loadNextPage = { cursor: String ->
                                    scope.launch {
                                        val page = getMessages(selectedRoom.id, cursor)
                                        if (page != null) {
                                            messagesState.addAll(0, page.messages.reversed())
                                            lastMessageSource.value = LoadMessageAction.LoadMore
                                            hasNextPage.value = page.hasNext
                                        } else {
                                            messagesState.clear()
                                        }
                                    }
                                }
                            )
                        } else {
                            Div { P { Text("Select a room") } }
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
                    scope.launch {
                        dispatch(state, Action.Redirect("/app"))
                    }
                }, onConfirm = {
                    scope.launch {
                        val r = validateInvite(invite.id)
                        if (r != null) {
                            modalSelectState.value = ModalSelect.None
                            dispatch(state, Action.GetRooms)
                        } else {
                            // TODO: error
                        }
                    }
                })
            }
        }

        ModalSelect.Disconnect -> DisconnectedModal {
            modalSelectState.value = ModalSelect.None
        }

        // TODO: review
        else -> {}
    }
}
