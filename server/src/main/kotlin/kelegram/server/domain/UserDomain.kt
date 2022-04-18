package kelegram.server.domain

import kelegram.common.NewUser
import kelegram.common.User
import kelegram.server.model.Session
import kelegram.server.data.SessionData
import kelegram.server.data.UserData
import java.time.LocalDateTime
import java.util.*

object UserDomain {
    suspend fun create(newUser: NewUser): User {
        val id = UUID.randomUUID().toString()
        val user = User(id, newUser.identityProvider, newUser.nickname, newUser.avatarUrl)
        UserData.add(user)
        return user
    }

    val getById = UserData::getById

    val getByIdFromProvider = UserData::getByIdFromProvider

    val getParticipatedRooms = UserData::getRooms

    suspend fun createSession(user: User): Session {
        val id = UUID.randomUUID().toString()
        val session = Session(id, user.id, LocalDateTime.now().toString())
        SessionData.add(session)
        return session
    }

    suspend fun removeSession(sessionId: String) {
        SessionData.remove(sessionId)
    }

    val getSession = SessionData::get
}