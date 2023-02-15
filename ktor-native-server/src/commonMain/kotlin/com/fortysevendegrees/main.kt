package com.fortysevendegrees

import arrow.continuations.SuspendApp
import arrow.continuations.ktor.server
import arrow.core.raise.either
import arrow.fx.coroutines.resourceScope
import com.fortysevendegrees.env.Env
import com.fortysevendegrees.env.env
import com.fortysevendegrees.routes.ping
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.maxAgeDuration
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

//import kotlinx.uuid.UUID
//import kotlinx.uuid.generateUUID
//import kotlinx.uuid.randomUUID

@Serializable
data class Payload(
  val id: String = Random.nextLong().toString(),
  val messages: List<String> = defaultMessages
) {
  companion object {
    val defaultMessages: List<String> = List(1000) { "Message $it" }
  }
}

fun main() = SuspendApp {
//  either {
    resourceScope {
//      val env = env()
//    @Suppress("UnusedPrivateMember")
//    val database = postgres(env.postgres)
      server(
        CIO,
        port = 8080,
        host = "0.0.0.0",
        preWait = 0.seconds
      ) {
//        install(CORS) {
//          allowHeader(HttpHeaders.Authorization)
//          allowHeader(HttpHeaders.ContentType)
//          allowNonSimpleContentTypes = true
//          maxAgeDuration = 3.days
//        }
//        install(ContentNegotiation) {
//          json(
//            Json {
//              isLenient = true
//              ignoreUnknownKeys = true
//            }
//          )
//        }
//        install(CallLogging)
        routing {
          ping()
          get("/readiness") { call.respondText("OK") }
//          get("/test1") { call.respond(Payload()) }
        }
      }
      awaitCancellation()
    }
//  }.onLeft { println(it.message) }
}
