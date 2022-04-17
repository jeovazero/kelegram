package kelegram.client.modals

import androidx.compose.runtime.Composable
import kelegram.client.ui.Variant
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun DisconnectedModal(
    onCancel: () -> Unit
) {
    Modal(
        title = "Sadge. :(",
        content = {
            P {
                Text("The connection with the server was lost. Try again later.")
            }
        },
        onClose = onCancel,
        actions = listOf(
            ModalAction("Okay",
                onClick = {
                    onCancel()
                },
                variant = Variant.Secondary),
        )
    )
}