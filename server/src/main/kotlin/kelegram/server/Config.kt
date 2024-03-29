package kelegram.server

object Config {
    val clientId = System.getenv("CLIENT_ID") ?: ""
    val clientSecret = System.getenv("CLIENT_SECRET") ?: ""
    val allowedOrigins = (System.getenv("ALLOWED_ORIGINS") ?: "http://localhost:8080").split(";")
    val mongodbUrl = System.getenv("MONGODB_URL") ?: "mongodb://localhost"
    val port = System.getenv("PORT")?.toInt() ?: 8000
    val oauthCallbackServer = System.getenv("CALLBACK_SERVER") ?: "http://localhost:8000"
}
