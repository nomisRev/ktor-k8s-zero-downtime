package com.fortysevendegrees

import com.fortysevendegrees.routes.ping
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication

class PingSpec : StringSpec({
  "ping route" {
    testApplication {
      application { routing { ping() } }
      val response = client.get("/health")
      response.status shouldBe HttpStatusCode.OK
    }
  }
})
