package com.fortysevendegrees.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.ping(): Route =
  get("/health") {
    call.respondText("Healthy", status = HttpStatusCode.OK)
  }
