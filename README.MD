# Webinar: Graceful Resource Handling Using Structured Concurrency in Kotlin

Modern programming often requires us to make strong guarantees about how our applications terminate. One way to do this is through the use of structured concurrency, which allows us to reason about parallel processes and how they relate to each other.

In this session, we will learn how to use Arrow’s Resource Domain-Specific Language (DSL) to reason about resource safety in the same way we use structured concurrency to avoid leaking resources. We will also see how this can be combined with KotlinX Coroutines to build complex use cases in a simple and elegant way using Kotlin DSLs.

## Kotlin Native Server

Libraries used:
 - [Ktor](https://ktor.io)
 - [CashApp SqlDelight](https://github.com/cashapp/sqldelight)
 - [Postgres Native SqlDelight](https://github.com/hfhbd/postgres-native-sqldelight)
 - [Arrow](https://arrow-kt.io)
 - [SuspendApp](https://github.com/arrow-kt/suspendapp)

Project can only be build from Linux, because of the Postgres C API. (If you figure out how to build it on MacOs (Arm64), please let me know)

```text
apt-get install libpq-dev
./gradlew build
docker build -t ktor-native-server:tag .
```

To run the `deployment` tasks, you need a local Kubernetes environment. The demo uses Docker Desktop (non-commercial) license.

```text
kubectl apply -f deployment/network.yaml
kubectl apply -f deployment/network.yaml
```
