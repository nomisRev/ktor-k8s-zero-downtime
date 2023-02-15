package com.fortysevendegrees.env

import app.softwork.sqldelight.postgresdriver.PostgresNativeDriver
import arrow.fx.coroutines.ResourceScope
import com.fortysevendegrees.sqldelight.NativePostgres

suspend fun ResourceScope.postgres(config: Env.Postgres): NativePostgres {
  val driver = install({
    PostgresNativeDriver(
      host = config.host,
      port = config.port,
      user = config.user,
      database = config.databaseName,
      password = config.password
    )
  }) { driver, _ -> driver.close() }
  return NativePostgres(driver).also {
    NativePostgres.Schema.create(driver).await()
  }
}
