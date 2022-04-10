package kelegram.server.data

import kelegram.server.model.Session
import org.litote.kmongo.*

val sessionCol = database.getCollection<Session>()

object SessionData {
    suspend fun add(sessionData: Session) {
        sessionCol.insertOne(sessionData)
    }

    suspend fun get(id: String): Session? {
        return sessionCol.findOne(Session::id eq id)
    }
}