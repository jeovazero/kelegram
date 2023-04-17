package kelegram.client.modals

import androidx.compose.runtime.Composable
import kelegram.client.ui.Variant
import kelegram.common.InviteInfo
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun AcceptInviteModal(
    invite: InviteInfo,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Modal(
        title = "Yay!!! An invite :)",
        content = {
            P {
                Text("You were invited by \"${invite.ownerName}\" to the \"${invite.roomName}\" Room")
            }
        },
        onClose = onCancel,
        actions = listOf(
            ModalAction("Let's GOOOOOO", onClick = onConfirm),
            ModalAction("Not today",
                onClick = {
                    onCancel()
                },
                variant = Variant.Secondary),
        )
    )
}