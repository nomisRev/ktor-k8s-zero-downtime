package com.fortysevendegrees

sealed interface ApplicationErrors {
  data class ConfigurationError(val message: String) : ApplicationErrors
}
