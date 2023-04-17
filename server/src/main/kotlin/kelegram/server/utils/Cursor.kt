package kelegram.server.utils


import kotlinx.datetime.LocalDateTime
import java.util.*

fun <A> effectOrNull(eff: () -> A): A? =
    try {
        eff()
    } catch (e: Exception) {
        null
    }

data class Cursor(val id: String, val createdAt: LocalDateTime) {
    object Companion {
        private fun toBase64(str: String): String =
            Base64.getEncoder().encodeToString(str.toByteArray())

        private fun fromBase64(str: String): String? =
            effectOrNull {
                String(Base64.getDecoder().decode(str))
            }

        fun decode(str: String): Cursor? {
            val r = fromBase64(str)?.split("$") ?: listOf()
            logger.debug { r.toString() }
            if (r.size != 2) return null

            val date = r[0].let { effectOrNull { LocalDateTime.parse(it) } } ?: return null
            val id = r[1]

            return Cursor(id, date)
        }

        fun Cursor.encode(): String =
            toBase64("${this.createdAt}$${this.id}")
    }
}