package com.fortysevendegrees.sqldelight.ktornativeserver

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.fortysevendegrees.UsersQueries
import com.fortysevendegrees.sqldelight.NativePostgres
import kotlin.Int
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<NativePostgres>.schema: SqlSchema
  get() = NativePostgresImpl.Schema

internal fun KClass<NativePostgres>.newInstance(driver: SqlDriver): NativePostgres =
    NativePostgresImpl(driver)

private class NativePostgresImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), NativePostgres {
  public override val usersQueries: UsersQueries = UsersQueries(driver)

  public object Schema : SqlSchema {
    public override val version: Int
      get() = 1

    public override fun create(driver: SqlDriver): QueryResult<Unit> {
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS users(
          |    id BIGSERIAL PRIMARY KEY,
          |    email VARCHAR(200) NOT NULL UNIQUE,
          |    username VARCHAR(100) NOT NULL UNIQUE,
          |    bio VARCHAR(1000) NOT NULL DEFAULT '',
          |    image VARCHAR(255) NOT NULL DEFAULT ''
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    public override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int,
      vararg callbacks: AfterVersion,
    ): QueryResult<Unit> = QueryResult.Unit
  }
}
