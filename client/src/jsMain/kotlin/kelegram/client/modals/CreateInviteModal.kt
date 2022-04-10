package kelegram.client.modals

import androidx.compose.runtime.*
import kelegram.client.ui.Variant
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.attributes.*

@Composable
fun CreateInviteModal(onConfirm: () -> Unit, onCancel: () -> Unit, inviteLink: String? = null) {
    Modal(
        title = "Create Invite",
        content = {
            Text("Creating an invite to the selected room")
            if (inviteLink != null) {
                Text("Invite created:")
                A (attrs={
                    href("#$inviteLink")
                }) {
                    Text("Invite")
                }
            }
        },
        onClose = onCancel,
        actions = listOf(
            ModalAction("Create Invite", onClick = {
                onConfirm()
            }),
            ModalAction("Nevermind",
                onClick = onCancel,
                variant = Variant.Neutral),
        )
    )
}
