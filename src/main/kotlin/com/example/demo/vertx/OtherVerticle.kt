package com.example.demo.vertx

import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import mu.KotlinLogging
import org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
@Scope(SCOPE_PROTOTYPE)
class OtherVerticle: CoroutineVerticle() {
    override suspend fun start() {
        logger.info { "Hello from: $this! Config: ${config.encodePrettily()}"}
    }
}