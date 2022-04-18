package kelegram.client

import androidx.compose.runtime.*
import kelegram.client.pages.LoadingPage
import kelegram.client.pages.MainPage
import kelegram.client.pages.LoginPage
import kelegram.client.pages.SignupPage
import kelegram.client.ui.AppWrapper
import kelegram.client.ui.ButtonStyle
import kelegram.client.ui.SpacingStyle
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.WebSocket
import org.w3c.dom.Window
import org.w3c.dom.events.Event

fun matchRoute(pattern: String, path: List<String>): Pair<Boolean, Map<String, String>?> {
    val patterns = pattern.split("/")
    val range = IntRange(0, patterns.size - 1)
    val params = arrayListOf<Pair<String, String>>()
    for (i in range) {
        val current = patterns[i]
        val currentPath = path.getOrElse(i, { "" })
        if (current.startsWith(":")) {
            params.add(Pair(current.drop(1), currentPath))
        } else if (current != currentPath) {
            return Pair(false, null)
        }
    }
    return Pair(true, mapOf(*params.toTypedArray()))

}

@Composable
fun Redirect(state: MState, path: String) {
    LaunchedEffect(path) {
        dispatch(state, Action.Redirect(path))
    }
}

val screenFromPath = { pathname: String ->
    val paths = pathname.split("/")
    val pathsTail = paths.drop(1)
    val (isLogin, _) = matchRoute("login", pathsTail)
    val (isSignup, _) = matchRoute("signup", pathsTail)
    val (isApp, _) = matchRoute("app", pathsTail)
    val (_, appInviteCtx) = matchRoute("app/invites/:inviteId", pathsTail)

    when {
        isLogin -> Pair(AppScreen.Login, null)
        isSignup -> Pair(AppScreen.SignUp, null)
        isApp -> Pair(AppScreen.Main, appInviteCtx)
        else -> Pair(AppScreen.None, null)
    }
}

@Composable
fun DevTag() {
    Div(attrs = {
        style {
            position(Position.Fixed)
            right(-6.cssRem)
            top(2.cssRem)
            property("rotate", "45deg")
            color(Color("#ffef69"))
            padding(5.px, 7.cssRem)
            backgroundColor(Color("#000"))
            property("z-index", 100);
            fontWeight("bold")
        }
    }) {
        Span{ Text("ALPHA") }
    }
}

fun main() {
    renderComposable(rootElementId = "root") {
        val pathname = window.location.pathname
        val state = remember {
            val (screen, params) = screenFromPath(pathname)
            mutableStateOf(State(screen = screen, routeParams = params))
        }
        val user = state.value.user

        LaunchedEffect(pathname) {
            val (screen, params) = screenFromPath(pathname)
            state.value = state.value.copy(screen = screen, routeParams = params)
        }
        LaunchedEffect(user) {
            if (user != null) {
                if (state.value.screen != AppScreen.Main) {
                    dispatch(state, Action.Redirect("/app"))
                }
                if (state.value.socket == null) {
                    val socket = WebSocket(url = KelegramServer.WebSocket)
                    state.value.socket = socket
                }
            }
        }
        DisposableEffect(state) {
            val listener = { e: Event ->
                val pathname1 = (e.target.asDynamic() as Window).location.pathname
                val (screen, _) = screenFromPath(pathname1)
                state.value = state.value.copy(screen = screen)
            }
            window.addEventListener("popstate", listener)

            onDispose {
                window.removeEventListener("popstate", listener)
            }
        }

        Style(ResetStylesheet)
        Style(KelegramStylesheet)
        Style(ButtonStyle)
        Style(SpacingStyle)
        AppWrapper {
            when (state.value.screen) {
                AppScreen.Loading -> LoadingPage(state)
                AppScreen.Login -> LoginPage(state)
                AppScreen.SignUp -> SignupPage(state)
                AppScreen.Main -> MainPage(state)
                else -> Redirect(state, "/app")
            }
            DevTag()
        }
    }
}