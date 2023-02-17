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

## The final result

To run the `deployment` tasks, you need a local Kubernetes environment. The demo uses Docker Desktop (non-commercial) license.

```text
kubectl apply -f deployment/network.yaml
kubectl apply -f deployment/postgres.yaml
```

Be sure to update `deployment/deploy.yaml` with the current ip address of postgres in your cluster.
Then you can apply the `deploy.yaml` file to your cluster.

```text
kubectl apply -f deployment/deploy.yaml
```

When the `ktor-native-server` pod are running, you can run the `post` and `get` tests in `apitest.http`.
Your `kubectl get all` should look similar to this.

```text
NAME                              READY   STATUS    RESTARTS   AGE
pod/ktornative-5b8c6bcdb5-54chv   1/1     Running   0          28m
pod/ktornative-5b8c6bcdb5-bnx62   1/1     Running   0          28m
pod/ktornative-5b8c6bcdb5-lc26b   1/1     Running   0          28m
pod/ktornative-5b8c6bcdb5-tqcdf   1/1     Running   0          28m
pod/postgres-78d65bf67-txg5b      1/1     Running   0          33m

NAME                   TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
service/kubernetes     ClusterIP      10.96.0.1       <none>        443/TCP          120m
service/loadbalancer   LoadBalancer   10.105.172.85   localhost     8080:30821/TCP   115m
service/postgres       ClusterIP      10.107.64.239   <none>        5432/TCP         33m

NAME                         READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/ktornative   4/4     4            4           28m
deployment.apps/postgres     1/1     1            1           33m

NAME                                    DESIRED   CURRENT   READY   AGE
replicaset.apps/ktornative-5b8c6bcdb5   4         4         4       28m
replicaset.apps/postgres-78d65bf67      1         1         1       33m
```

### The intermediate results

See the webinar for more details.

## Step 1

We start with a simple Ktor server, which we will deploy to Kubernetes.
We use a variation of [the Hello World example for Ktor with Kotlin/Native](https://github.com/ktorio/ktor-documentation/tree/2.2.3/codeSnippets/snippets/embedded-server-native ), and perform a rolling update between two versions of the same code, code used in this step can be found in the `step-1` branch.

```text
Transactions:		        2521 hits
Availability:		       98.55 %
Elapsed time:		       12.04 secs
```

## Step 2
We update the original code to use the `SuspendApp` library, and `Resource` from Arrow to close the embedded server gracefully.
Then we perform the same test again.

```text
Transactions:		        1620 hits
Availability:		       99.39 %
Elapsed time:		       22.93 secs
```

## Step 3
We use update the code to use SuspendApp Ktor integration `server` instead of `embeddedServer`,
this introduces a small `delay` for the network (LoadBalancer/Ingress) to remove our pods from the IP tables.

This code can be found in the `step-3` branch.

Additional references on why this is needed:
 - [AWS Load Balancer](https://github.com/kubernetes-sigs/aws-load-balancer-controller/issues/1719#issuecomment-1122271908)
 - [Kubernetes' dirty endpoint secret and Ingress](https://philpearl.github.io/post/k8s_ingress/)
 - [Graceful shutdown and zero downtime deployments in Kubernetes](https://learnk8s.io/graceful-shutdown)

```text
Transactions:		       21618 hits
Availability:		      100.00 %
Elapsed time:		       38.04 secs
```

## Step 5

- Image on DockerHub: vergauwensimon/ktor-native-server:20230215-222243

We add `Http` & `Postgres` configuration using Kotlin's `getenv` and Arrow `Either`/`Raise` to accumulate errors.
If you run `deploy.yaml` with the `env` section commented out,
you will see that the `ktor-native-server` will fail to start showing all the environment variables as missing.

## Step 5

We uncomment `env` variables, and set up `NativePostgres` capturing the exception and turning it into a _typed error_, or it will make Kotlin Native hang in docker.
Using the `NativePostgres` we can now wire our routes to the database, and our final result is a working server.

This code can be found on the `main` branch.
