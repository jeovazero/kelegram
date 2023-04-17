package kelegram.server.oauth

import kelegram.common.NewUser
import kelegram.common.IdentityProvider
import kelegram.common.Provider
import kelegram.server.Config
import kelegram.server.domain.UserDomain
import kelegram.server.routes.SESSION_COOKIE
import kotlinx.coroutines.runBlocking
import org.http4k.client.ApacheClient
import org.http4k.core.*
import org.http4k.core.cookie.cookie
import org.http4k.format.Moshi.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.security.OAuthProvider
import org.http4k.security.OAuthProviderConfig
import kotlinx.serialization.Serializable
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite

@Serializable
data class UserGithub(
    val name: String?,
    val login: String,
    val avatar_url: String?,
    val bio: String?,
)

val userGithubLens = Body.auto<UserGithub>().toLens()

val persistence = InsecureCookieBasedOAuthPersistence("oauthTest")

fun githubOAuth(
    oauthServerUri: Uri,
    credentials: Credentials,
    oauthServerHttp: HttpHandler = ApacheClient()
): RoutingHttpHandler {
    val oAuthProvider = OAuthProvider(
        OAuthProviderConfig(
            oauthServerUri,
            "/login/oauth/authorize",
            "/login/oauth/access_token",
            credentials
        ),
        oauthServerHttp,
        Uri.of("${Config.oauthCallbackServer}/callback"),
        listOf("read:user", "user:email"),
        persistence
    )
    return routes(
        "/callback" bind Method.GET to oAuthProvider.callback,
        "/login" bind Method.GET to oAuthProvider.authFilter.then {
            println(it)
            val token = it.cookie("oauthTestAccessToken")?.value
            val referer = it.header("Referer")
            if (token != null && referer != null) {
                val bearer = "Bearer $token"
                val response = ApacheClient()(
                    Request(Method.GET, "https://api.github.com/user")
                        .header("Authorization", bearer)
                )
                val userInfo: UserGithub? = userGithubLens(response)
                val idFromProvider = userInfo?.login
                val username = userInfo?.name ?: userInfo?.login
                val avatarUrl = userInfo?.avatar_url
                // create session or user
                val user =
                    idFromProvider?.let {
                        runBlocking {
                            UserDomain.getByIdFromProvider(idFromProvider)
                        }
                    }
                if (user != null) {
                    val session = runBlocking { UserDomain.createSession(user) }
                    println("Has User $user")
                    Response(FOUND)
                        .cookie(Cookie(SESSION_COOKIE, session.id))
                        .header("location", "$referer?id=${session.id}")
                } else {
                    if (username != null && idFromProvider != null) {
                        val userSession = runBlocking {
                            UserDomain.create(
                            NewUser(
                                username,
                                IdentityProvider(idFromProvider, Provider.Github), avatarUrl)
                            )
                        }
                        val session = userSession.session
                        println("New User $user")
                        Response(FOUND)
                            .cookie(Cookie(SESSION_COOKIE, session.id, secure = true, httpOnly = true, sameSite = SameSite.None))
                            .header("location", "$referer?id=${session.id}")
                    } else {
                        Response(OK).body("Hello! ${userInfo?.login}\nBio: ${userInfo?.bio}")
                    }
                }
            } else {
                Response(INTERNAL_SERVER_ERROR).body("there is something wrong; Referer: $referer")
            }
        }
    )
}
