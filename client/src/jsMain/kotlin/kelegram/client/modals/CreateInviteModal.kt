package kelegram.client.modals

import androidx.compose.runtime.Composable
import kelegram.client.ui.Variant
import org.jetbrains.compose.web.dom.Text

@Composable
fun CreateInviteModal(onConfirm: () -> Unit, onCancel: () -> Unit) {
    Modal(
        title = "Create Invite",
        content = {
            Text("Creating an invite to the selected room")
        },
        onClose = onCancel,
        actions = listOf(
            ModalAction("Create Invite", onClick = onConfirm),
            ModalAction("Nevermind",
                onClick = onCancel,
                variant = Variant.Neutral),
        )
    )
}