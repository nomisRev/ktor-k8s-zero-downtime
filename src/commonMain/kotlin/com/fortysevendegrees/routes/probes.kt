package com.fortysevendegrees.routes

import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.probes() {
  get("/readiness") { call.respondText("OK") }
  // TODO Check Postgres connection
  get("/health") { call.respondText("OK") }
}
