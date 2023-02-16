package com.fortysevendegrees

import arrow.continuations.SuspendApp
import arrow.continuations.ktor.server
import arrow.core.raise.either
import arrow.fx.coroutines.resourceScope
import com.fortysevendegrees.env.env
import com.fortysevendegrees.env.postgres
import com.fortysevendegrees.routes.probes
import com.fortysevendegrees.routes.users
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.awaitCancellation
import kotlinx.serialization.json.Json

fun main() = SuspendApp {
  either {
    resourceScope {
      val env = env()
      val database = postgres(env.postgres).bind()
      server(CIO, port = env.http.port, host = env.http.host, preWait = 5.seconds) {
        setup()
        routing {
          probes()
          users(database)
        }
      }
      awaitCancellation()
    }
  }.onLeft { error ->
    when (error) {
      is ServerError.ConfigurationError -> println(error.message)
      is ServerError.PostgresError -> error.state.printStackTrace()
    }
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
