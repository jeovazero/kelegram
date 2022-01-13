package kelegram.server.domain

import kelegram.server.persistence.MessagePersistence

object MessageDomain {
    val add = MessagePersistence::add
}