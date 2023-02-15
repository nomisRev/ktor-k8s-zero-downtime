package com.fortysevendegrees.sqldelight

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.fortysevendegrees.UsersQueries
import com.fortysevendegrees.sqldelight.ktornativeserver.newInstance
import com.fortysevendegrees.sqldelight.ktornativeserver.schema

public interface NativePostgres : Transacter {
  public val usersQueries: UsersQueries

  public companion object {
    public val Schema: SqlSchema
      get() = NativePostgres::class.schema

    public operator fun invoke(driver: SqlDriver): NativePostgres =
        NativePostgres::class.newInstance(driver)
  }
}
