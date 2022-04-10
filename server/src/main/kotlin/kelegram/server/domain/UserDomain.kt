package kelegram.server.domain

import kelegram.common.NewUser
import kelegram.common.User
import kelegram.server.data.SessionData
import kelegram.server.data.UserData
import kelegram.server.model.Session
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
        val session = Session(id, user.id)
        SessionData.add(session)
        return session
    }

    val getSession = SessionData::get
}