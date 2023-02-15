@Suppress("DSL_SCOPE_VIOLATION") plugins {
  alias(libs.plugins.kotest.multiplatform)
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.kover)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.sqldelight)
}

group = "com.fortysevendegrees"
version = "0.1"

repositories {
  mavenCentral()
  mavenLocal()
}

val buildDockerImage by tasks.registering(Exec::class) {
  dependsOn(tasks.findByName("linkReleaseExecutableLinuxX64"))
  commandLine("docker", "build", "--platform", "linux/amd64", "-t", "ktor-native-server:$version", ".")
}

sqldelight {
  databases {
    create("NativePostgres") {
      packageName.set("com.fortysevendegrees.sqldelight")
      dialect(libs.postgres.native.dialect.get())
    }
  }
  linkSqlite.set(false)
}

kotlin {
  linuxX64 {
    binaries {
      executable { entryPoint = "com.fortysevendegrees.main" }
    }
  }

//  macosArm64 {
//    binaries {
//      executable { entryPoint = "com.fortysevendegrees.main" }
//    }
//  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.arrow.fx)
        implementation(libs.suspendapp)
        implementation(libs.suspendapp.ktor)
        implementation(libs.bundles.ktor.server)
        implementation(libs.postgres.native.driver)
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
