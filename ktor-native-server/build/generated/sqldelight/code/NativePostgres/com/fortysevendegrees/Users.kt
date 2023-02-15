package com.fortysevendegrees

import kotlin.Long
import kotlin.String

public data class Users(
  public val id: Long,
  public val email: String,
  public val username: String,
  public val bio: String,
  public val image: String,
)
