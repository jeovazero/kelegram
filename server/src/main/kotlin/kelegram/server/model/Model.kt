package kelegram.server.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val mapper = jacksonObjectMapper()

data class NewUser(
    val nickname: String,
    val identityProvider: IdentityProvider,
    val avatarUrl: String? = null,
)
data class IdentityProvider(val id: String, val provider: Provider)

enum class Provider {
    Github,
    Fake
}
data class User(
    val id: String,
    val identityProvider: IdentityProvider,
    val nickname: String,
    val avatarUrl: String?,
    val roomsIds: List<String> = listOf(),
)

data class Session(
    val id: String,
    val userId: String
)