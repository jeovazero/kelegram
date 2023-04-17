package kelegram.server.domain

import kelegram.common.NewUser
import kelegram.common.User
import kelegram.server.model.Session
import kelegram.server.data.SessionData
import kelegram.server.data.UserData
import java.time.LocalDateTime
import java.util.*

data class UserSession(val user: User, val session: Session)

object UserDomain {
    suspend fun create(newUser: NewUser): UserSession {
        val id = UUID.randomUUID().toString()
        val user = User(id, newUser.identityProvider, newUser.nickname, newUser.avatarUrl)
        UserData.add(user)
        val session = createSession(user)
        return UserSession(user, session)
    }

    val getById = UserData::getById

    val getByIdFromProvider = UserData::getByIdFromProvider

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