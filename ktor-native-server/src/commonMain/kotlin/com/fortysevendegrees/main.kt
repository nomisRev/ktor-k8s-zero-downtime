package com.fortysevendegrees

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.resourceScope
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlin.random.Random
import kotlinx.coroutines.awaitCancellation
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
  resourceScope {
    install({
      embeddedServer(CIO, port = 8080) {
        setup()
        routing {
          get("/readiness") { call.respondText("OK") }
          get("/test") { call.respond(Payload()) }
        }
      }.also { it.start() }
    }) { engine, _ -> engine.stop(500, 500) }
    awaitCancellation()
  }
}

fun Application.setup() {
  install(ContentNegotiation) {
    json(
      Json {
        isLenient = true
        ignoreUnknownKeys = true
      }
    )
  }
}
