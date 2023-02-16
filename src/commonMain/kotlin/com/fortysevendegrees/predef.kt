package com.fortysevendegrees

import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext

typealias KtorCtx = PipelineContext<Unit, ApplicationCall>
