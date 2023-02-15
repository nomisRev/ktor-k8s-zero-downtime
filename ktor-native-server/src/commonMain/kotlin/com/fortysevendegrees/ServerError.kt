package com.fortysevendegrees

sealed interface ServerError {
  data class ConfigurationError(val message: String) : ServerError
}
