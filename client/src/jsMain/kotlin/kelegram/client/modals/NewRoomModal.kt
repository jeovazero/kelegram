package kelegram.client.pages

import androidx.compose.runtime.Composable
import kelegram.client.modals.Modal
import kelegram.client.modals.ModalAction
import kelegram.client.modals.ModalStylesheet
import kelegram.client.ui.Variant
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Input
import org.w3c.dom.HTMLInputElement

@Composable
fun NewRoomModal(onConfirm: (String) -> Unit, onCancel: () -> Unit) {
    Modal(
        title = "New Room",
        content = {
            Input(type = InputType.Text, attrs = {
                classes(ModalStylesheet.input)
                placeholder("Room name...")
                name("roomname")
            })
        },
        onClose = onCancel,
        actions = listOf(
            ModalAction("Create Room", onClick = {
                val roomname =
                    document.querySelector("input[name=roomname]") as HTMLInputElement
                onConfirm(roomname.value)
            }),
            ModalAction("Nevermind",
                onClick = onCancel,
                variant = Variant.Neutral),
        )
    )
}
