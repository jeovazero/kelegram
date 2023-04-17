package kelegram.client.modals

import androidx.compose.runtime.*
import kelegram.client.tokens.Token
import kelegram.client.ui.Variant
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*

object CreateInviteStylesheet : StyleSheet() {
    val content by style {
        child(self, type("p")) style {
            margin(8.px, 0.px)
        }
        child(self, id("invite-link")) style {
            color(Token.pallete.primaryPair)
        }
    }
}
@Composable
fun CreateInviteModal(onConfirm: () -> Unit, onCancel: () -> Unit, inviteLink: String? = null) {
    Style(CreateInviteStylesheet)
    val copied = remember { mutableStateOf(false) }

    Modal(
        title = "Create Invite",
        content = {
            Span { Text("Create an invite to the selected room") }
            if (inviteLink != null) {
                Div(attrs = {classes(CreateInviteStylesheet.content)}) {
                    P { Text("Copy the invite:") }
                    A(attrs = {

                    }) {
                        P(attrs = {id("invite-link")}) { Text("${window.location.host}/app$inviteLink") }
                    }
                    if (copied.value) {
                        P { Text("Copied!!!") }
                    }
                }
            }
        },
        onClose = onCancel,
        actions = listOf(
            ModalAction("Create Invite", hide = inviteLink != null, onClick = {
                onConfirm()
            }),
            ModalAction("Copy Link", hide = inviteLink == null, onClick = {
                window.navigator.clipboard.writeText("${window.location.host}/app$inviteLink")
                copied.value = true
            }),
            ModalAction(
                "Nevermind",
                onClick = onCancel,
                variant = Variant.Neutral
            )
        )
    )
}
