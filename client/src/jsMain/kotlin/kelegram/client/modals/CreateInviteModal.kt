package kelegram.client.modals

import androidx.compose.runtime.*
import kelegram.client.ui.Variant
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.attributes.*

@Composable
fun CreateInviteModal(onConfirm: () -> Unit, onCancel: () -> Unit, inviteLink: String? = null) {
    Modal(
        title = "Create Invite",
        content = {
            Span { Text("Create an invite to the selected room") }
            if (inviteLink != null) {
                P { Text("Copy the invite:") }
                A(attrs = {
                    href("/app$inviteLink")
                }) {
                    Text("${window.location.host}/app$inviteLink")
                }
            }

        },
        onClose = onCancel,
        actions = listOf(
            ModalAction("Create Invite", onClick = {
                onConfirm()
            }),
            ModalAction(
                "Nevermind",
                onClick = onCancel,
                variant = Variant.Neutral
            ),
        )
    )
}
