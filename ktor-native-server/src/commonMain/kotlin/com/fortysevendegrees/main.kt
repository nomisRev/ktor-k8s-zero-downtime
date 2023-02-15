package com.fortysevendegrees

import arrow.continuations.SuspendApp
import arrow.continuations.ktor.server
import arrow.core.raise.either
import arrow.fx.coroutines.resourceScope
import com.fortysevendegrees.env.env
import com.fortysevendegrees.env.postgres
import com.fortysevendegrees.routes.getUser
import com.fortysevendegrees.routes.postUser
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.awaitCancellation
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun main() = SuspendApp {
  either {
    resourceScope {
      val env = env()
      val database = postgres(env.postgres)
      server(CIO, port = env.http.port, host = env.http.host, preWait = 5.seconds) {
        setup()
        routing {
          get("/readiness") { call.respondText("OK") }
          postUser(database)
          getUser(database)
        }
      }
      awaitCancellation()
    }
  }.onLeft { println(it.message) }
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
