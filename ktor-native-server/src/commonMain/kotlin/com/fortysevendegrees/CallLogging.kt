package com.fortysevendegrees

import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.log
import io.ktor.server.request.uri

val CallLogging: ApplicationPlugin<Unit> = createApplicationPlugin(
  "CallLogging",
  { }
) {
  val logger = application.log
  onCall { call ->
    logger.debug("Call: ${call.request.uri}")
  }
  onCallRespond { call, _ ->
    logger.debug("Response: ${call.response.status()}")
  }
}