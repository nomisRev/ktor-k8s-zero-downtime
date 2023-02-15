@Suppress("DSL_SCOPE_VIOLATION") plugins {
  alias(libs.plugins.kotest.multiplatform)
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.kover)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.sqldelight)
}

group = "com.fortysevendegrees"
version = "0.4"

repositories {
  mavenCentral()
}

val buildDockerImage by tasks.registering(Exec::class) {
  dependsOn(tasks.findByName("linkReleaseExecutableLinuxX64"))
  commandLine("docker", "build", "--platform", "linux/amd64", "-t", "ktor-native-server:$version", ".")
}

kotlin {
  linuxX64 {
    binaries {
      executable { entryPoint = "com.fortysevendegrees.main" }
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.arrow.fx)
        implementation(libs.suspendapp)
        implementation(libs.bundles.ktor.server)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(libs.bundles.kotest)
        implementation(libs.ktor.server.tests)
      }
    }
  }
}
