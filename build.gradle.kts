plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqldelight)
}

group = "com.fortysevendegrees"
version = "0.1"

repositories {
    mavenCentral()
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
    }
}
