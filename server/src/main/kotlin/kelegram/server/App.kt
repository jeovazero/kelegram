package kelegram.server

import kelegram.server.oauth.githubOAuth
import kelegram.server.routes.*
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.http4k.core.Credentials
import org.http4k.core.Method.*
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.*
import org.http4k.routing.routes
import org.http4k.server.PolyHandler

fun main() {
    val oauth = githubOAuth(
        Uri.of("https://github.com"),
        Credentials(Config.clientId, Config.clientSecret)
    )

    println(Config.allowedOrigins)

    val http = ServerFilters.Cors(
        CorsPolicy(OriginPolicy.AnyOf(Config.allowedOrigins),
            headers = listOf("Content-Type","Origin"),
            methods = listOf(GET,POST,PUT,OPTIONS),
            credentials = true
        )).then(
        routes(
            inviteRoutes(),
            roomRoutes(),
            ownedRoomRoutes(),
            accountRoutes(),
            oauth
        )
    )
    val ws = webSocket()

    PolyHandler(http, ws).asServer(Netty(8000)) .start()
}