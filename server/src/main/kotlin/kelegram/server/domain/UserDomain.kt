package kelegram.server.domain

import kelegram.common.NewUser
import kelegram.common.User
import kelegram.server.persistence.UserPersistence
import java.util.*

object UserDomain {
    suspend fun add(newUser: NewUser): User {
        val id = UUID.randomUUID().toString()
        val user = User(newUser.nickname, id)
        UserPersistence.add(user)
        return user
    }

    val get = UserPersistence::get

    val getParticipatedRooms = UserPersistence::getRooms
}