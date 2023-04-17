package kelegram.server.data

import kelegram.server.Config
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val client = KMongo.createClient(Config.mongodbUrl).coroutine
val database = client.getDatabase("test")
