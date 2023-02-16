package com.fortysevendegrees.env

import app.softwork.sqldelight.postgresdriver.PostgresNativeDriver
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.fx.coroutines.ResourceScope
import com.fortysevendegrees.ServerError.PostgresError
import com.fortysevendegrees.sqldelight.NativePostgres

suspend fun ResourceScope.postgres(config: Env.Postgres): Either<PostgresError, NativePostgres> = either {
  val driver = catch({
    install({
      PostgresNativeDriver(
        host = config.host,
        port = config.port,
        user = config.user,
        database = config.databaseName,
        password = config.password
      )
    }) { driver, _ -> driver.close() }
  }) { illegal: IllegalArgumentException -> raise(PostgresError(illegal)) }
  NativePostgres(driver).also {
    NativePostgres.Schema.create(driver).await()
  }
}
