package kelegram.client

import kelegram.common.*
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.js.json
import kotlinx.serialization.json.Json as JsonBase
import kotlinx.serialization.encodeToString
import org.w3c.dom.events.Event

object KelegramServer {
    val BASE = js("SERVER").unsafeCast<String>()
    private val SERVER = BASE.split("//").component2()
    private val s = if (SERVER.startsWith("https")) "s" else ""
    val WebSocket = "wss://$SERVER/kek"
}

suspend fun request(
    path: String = "/",
    method: String = "GET",
    body: dynamic = null,
): Response? {
    val r  = try {
        window.fetch(KelegramServer.BASE + path, object : RequestInit {
            override var method: String? = method
            override var body: dynamic = body
            override var headers: dynamic = json(
                "Accept" to "application/json",
                "Content-Type" to "application/json"
            )
            override var credentials: RequestCredentials? =
                RequestCredentials.Companion.INCLUDE
        }).catch {
            console.error("$it")
            null
        }
    } catch (e: Error) {
        console.error("ERRO $e")
        null
    }
    return r?.await()
}


val Json = JsonBase {
    ignoreUnknownKeys = true
}

suspend fun me(): User? {
    val r = try {
        request(
            path = "/me"
        )
    } catch (e: Error) {
        console.error("ERROR $e")
        return null
    }

    if (r != null && r.ok) {
        val w = r.text().await()
        return Json.decodeFromString(w)
    }

    return null
}

suspend fun logout(): Boolean {
    val r = try {
        request(path = "/logout")
    } catch (e: Error) {
        console.error("ERROR $e")
        return false
    }

    if (r != null) {
        return r.ok
    }
    return false
}

suspend fun getRooms(): List<Room>? {
    val r = request(path = "/rooms")
    if (r != null && r.ok) {
        val w = r.text().await()
        return Json.decodeFromString(w)
    }
    return null
}

suspend fun getMessages(id: String): List<MessageInfo>? {
    val r = request(
        path = "/rooms/$id/messages",
    )
    if (r != null && r.ok) {
        val w = r.text().await()
        return Json.decodeFromString(w)
    }
    return null
}

suspend fun getMembers(id: String): List<UserInfo>? {
    val r = request(
        path = "/rooms/$id/members",
    )
    if (r != null && r.ok) {
        val w = r.text().await()
        return Json.decodeFromString(w)
    }
    return null
}

suspend fun createAccount(nickname: String): Boolean {
    val r = request(
        path = "/account",
        method = "POST",
        body = JsonBase.encodeToString(NewUser(nickname,
            IdentityProvider(nickname,Provider.Fake)))
    )
    return r != null && r.ok
}

suspend fun createRoom(roomName: String): String? {
    val r = request(
        path = "/ownedrooms",
        method = "POST",
        body = JSON.stringify(json("name" to roomName))
    )
    if (r != null && r.ok) {
        return r.text().await()
    }
    return null
}

suspend fun createInvite(roomId: String): String? {
    val r = request(
        path = "/ownedrooms/$roomId/invites",
        method = "POST"
    )
    if (r != null && r.ok) {
        return r.text().await()
    }
    return null
}

suspend fun validateInvite(inviteId: String): Room? {
    val r = request(
        path = "/invites/$inviteId",
        method = "POST"
    )
    if (r != null && r.ok) {
        val w = r.text().await()
        return Json.decodeFromString(w)
    }
    return null
}

suspend fun getInvite(inviteId: String): InviteInfo? {
    val r = request(
        path = "/invites/$inviteId",
        method = "GET"
    )
    if (r != null && r.ok) {
        val w = r.text().await()
        return Json.decodeFromString(w)
    }
    return null
}

suspend fun defineMe(mstate: MState) {
    val user = me()
    if (user != null) {
        mstate.value = mstate.value.copy(user = user)
    } else {
        mstate.value = mstate.value.copy(user = null)
        dispatch(mstate,Action.Redirect("/login"))
    }
}

sealed class Action {
    object DefineMe : Action()
    data class CreateAccount(val nickname: String) : Action()
    data class CreateRoom(val roomName: String) : Action()
    object GetRooms : Action()
    data class SetRoom(val room: Room) : Action()
    data class Redirect(val path: String): Action()
    object Logout : Action()
}

suspend fun dispatch(mstate: MState, action: Action) {
    when (action) {
        is Action.DefineMe -> defineMe(mstate)
        is Action.Logout -> {
            logout()
            mstate.value = mstate.value.copy(user = null)
            dispatch(mstate, Action.Redirect("/login"))
        }
        is Action.CreateAccount -> {
            val result = createAccount(action.nickname)
            if (result) {
                defineMe(mstate)
            } else {
                // TODO: what to do when there is an error?
            }
        }
        is Action.GetRooms -> {
            val r = getRooms()
            val nextState = mstate.value.copy()
            if (r != null) {
                nextState.rooms = r
                mstate.value = nextState
            } else {
                // TODO: what to do when there is an error?
            }
        }
        is Action.CreateRoom -> {
            val r = createRoom(action.roomName)
            if (r != null) {
                dispatch(mstate, Action.GetRooms)
            }
        }
        is Action.SetRoom -> {
            val list = getMessages(action.room.id)
            val nextState = mstate.value.copy()
            if (list != null) {
                nextState.messages.clear()
                nextState.messages.addAll(list)
            } else {
                nextState.messages.clear()
            }
            nextState.selectedRoom = action.room
            mstate.value = nextState
        }
        is Action.Redirect -> {
            window.history.pushState(null, "", action.path)
            window.dispatchEvent(Event("popstate"))
        }
    }
}