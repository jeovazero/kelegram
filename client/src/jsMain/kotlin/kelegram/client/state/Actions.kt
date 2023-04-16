package kelegram.client.state

import kelegram.client.service.Service.createAccount
import kelegram.client.service.Service.createRoom
import kelegram.client.service.Service.getMessages
import kelegram.client.service.Service.getRooms
import kelegram.client.service.Service.logout
import kelegram.client.service.Service.me
import kelegram.common.Room
import kotlinx.browser.window
import org.w3c.dom.events.Event

suspend fun defineMe(mstate: MState) {
    val user = me()
    if (user != null) {
        mstate.value = mstate.value.copy(user = user)
    } else {
        mstate.value = mstate.value.copy(user = null)
        dispatch(mstate, Action.Redirect("/login"))
    }
}

sealed class Action {
    object DefineMe : Action()
    data class CreateAccount(val nickname: String) : Action()
    data class CreateRoom(val roomName: String) : Action()
    object GetRooms : Action()
    data class SetRoom(val room: Room) : Action()
    data class Redirect(val path: String) : Action()
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
            val page = getMessages(action.room.id)
            val nextState = mstate.value.copy()
            if (page != null) {
                nextState.messages.clear()
                nextState.messages.addAll(page.messages)
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