package com.example.demo.vertx

import io.vertx.config.ConfigRetriever
import io.vertx.kotlin.core.deploymentOptionsOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class DeploymentVerticle(
    private val configStore: ConfigRetriever,
    private val otherVerticle: OtherVerticle
): CoroutineVerticle() {
    override suspend fun start() {
        val config = configStore.config.await()

        logger.info { "Config: ${config.encodePrettily()}" }

        vertx.deployVerticle(otherVerticle, deploymentOptionsOf(config = config.getJsonObject("vertx"))).await()

        logger.info { "Deployed injected!" }
    }
}