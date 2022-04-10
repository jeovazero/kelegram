package kelegram.server.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val id: String,
    val userId: String
)