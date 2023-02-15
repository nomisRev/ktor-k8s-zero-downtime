package com.fortysevendegrees

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.softwork.sqldelight.postgresdriver.PostgresCursor
import app.softwork.sqldelight.postgresdriver.PostgresPreparedStatement
import kotlin.Any
import kotlin.Long
import kotlin.String
import kotlin.Unit

public class UsersQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun insertAndGetId(
    email: String,
    username: String,
    bio: String,
    image: String,
  ): ExecutableQuery<Long> = InsertAndGetIdQuery(email, username, bio, image) { cursor ->
    check(cursor is PostgresCursor)
    cursor.getLong(0)!!
  }

  public fun <T : Any> selectById(id: Long, mapper: (
    email: String,
    username: String,
    bio: String,
    image: String,
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    check(cursor is PostgresCursor)
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!
    )
  }

  public fun selectById(id: Long): Query<SelectById> = selectById(id) { email, username, bio,
      image ->
    SelectById(
      email,
      username,
      bio,
      image
    )
  }

  private inner class InsertAndGetIdQuery<out T : Any>(
    public val email: String,
    public val username: String,
    public val bio: String,
    public val image: String,
    mapper: (SqlCursor) -> T,
  ) : ExecutableQuery<T>(mapper) {
    public override fun <R> execute(mapper: (SqlCursor) -> R): QueryResult<R> =
        driver.executeQuery(1495379018, """
    |INSERT INTO users(email, username, bio, image)
    |VALUES ($1, $2, $3, $4)
    |RETURNING id
    """.trimMargin(), mapper, 4) {
      check(this is PostgresPreparedStatement)
      bindString(0, email)
      bindString(1, username)
      bindString(2, bio)
      bindString(3, image)
    }

    public override fun toString(): String = "Users.sq:insertAndGetId"
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    public override fun addListener(listener: Query.Listener): Unit {
      driver.addListener(listener, arrayOf("users"))
    }

    public override fun removeListener(listener: Query.Listener): Unit {
      driver.removeListener(listener, arrayOf("users"))
    }

    public override fun <R> execute(mapper: (SqlCursor) -> R): QueryResult<R> =
        driver.executeQuery(-1444918939, """
    |SELECT email, username, bio, image
    |FROM users
    |WHERE id = $1
    """.trimMargin(), mapper, 1) {
      check(this is PostgresPreparedStatement)
      bindLong(0, id)
    }

    public override fun toString(): String = "Users.sq:selectById"
  }
}
