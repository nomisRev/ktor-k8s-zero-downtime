package com.fortysevendegrees

sealed interface ServerError {
  data class ConfigurationError(val message: String) : ServerError
  data class PostgresError(val state: IllegalArgumentException): ServerError
}
