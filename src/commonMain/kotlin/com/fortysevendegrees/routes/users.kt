package com.fortysevendegrees.routes

import arrow.core.EitherNel
import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.effect
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.raise.fold
import arrow.core.raise.recover
import arrow.core.raise.zipOrAccumulate
import com.fortysevendegrees.KtorCtx
import com.fortysevendegrees.sqldelight.NativePostgres
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Long, val username: String, val email: String)
data class RegisterUser(val username: String, val email: String)

sealed interface UserError
data class IncorrectParams(val message: String) : UserError
data class UserAlreadyExists(val user: RegisterUser) : UserError
data class UserNotFound(val id: Long) : UserError

fun Routing.users(postgres: NativePostgres) {
  postUser(postgres)
  getUser(postgres)
}

private fun Routing.postUser(postgres: NativePostgres): Route =
  post("/user") {
    respond(Created) {
      val input = recover({ user().bind() }) { raise(IncorrectParams(it.joinToString())) }
      val id = catch({
        postgres.usersQueries.insertAndGetId(input.email, input.username).executeAsOne()
      }) { _: Throwable -> raise(UserAlreadyExists(input)) }
      User(id, input.username, input.email)
    }
  }

private fun Routing.getUser(postgres: NativePostgres): Route =
  get("/user") {
    respond(OK) {
      val id = ensureNotNull(call.parameters["id"]?.toLongOrNull()) { IncorrectParams("Valid id is required") }
      val user = postgres.usersQueries.selectById(id) { username, email ->
        User(id, username, email)
      }.executeAsOneOrNull()
      ensureNotNull(user) { IncorrectParams("User with id $id not found") }
      User(user.id, user.username, user.email)
    }
  }

private fun KtorCtx.user(): EitherNel<String, RegisterUser> = either {
  zipOrAccumulate(
    { ensureNotNull(call.parameters["username"]) { "Username is required" } },
    { ensureNotNull(call.parameters["email"]) { "Email is required" } },
  ) { username, email ->
    RegisterUser(username, email)
  }
}

private suspend inline fun <reified A : Any> KtorCtx.respond(
  statusCode: HttpStatusCode,
  noinline action: suspend Raise<UserError>.() -> A
): Unit = effect(action).fold(
  { error ->
    when (error) {
      is IncorrectParams -> call.respond(BadRequest, error.message)
      is UserAlreadyExists -> call.respond(Conflict, "${error.user} already exists")
      is UserNotFound -> call.respond(NotFound, "User with id ${error.id} not found")
    }
  }) { a -> call.respond(statusCode, a) }
