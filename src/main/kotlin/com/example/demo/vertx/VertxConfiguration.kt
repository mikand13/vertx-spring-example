package com.example.demo.vertx

import com.example.demo.vertx.SpringVerticleFactory.Companion.PREFIX
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetriever.create
import io.vertx.core.Vertx
import io.vertx.kotlin.config.configRetrieverOptionsOf
import io.vertx.kotlin.config.configStoreOptionsOf
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.awaitResult
import javax.annotation.PreDestroy
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

private val logger = KotlinLogging.logger {}

@Configuration
class VertxConfiguration(
    private val environment: Environment,
    private val verticleFactory: SpringVerticleFactory
) : InitializingBean {
    @Bean
    fun vertx(): Vertx = vertx

    @Bean
    fun configStore(): ConfigRetriever {
        val store = configStoreOptionsOf(
            type = "file",
            format = "yaml",
            config = jsonObjectOf(
                "path" to "application.yml"
            )
        )

        return create(vertx, configRetrieverOptionsOf(stores = listOf(store)))
    }

    @PreDestroy
    fun shutdownVertx() = runBlocking {
        logger.info { "Shutting down Vert.x..." }

        configStore().close()
        awaitResult<Void> { vertx.close(it) }

        logger.info { "Vert.x shutdown!" }
    }

    private fun deployVerticle(verticleName: String) =
        vertx.deployVerticle("$PREFIX:$verticleName")

    override fun afterPropertiesSet() {
        logger.info { "Starting local Vert.x..." }

        vertx = Vertx.vertx()
        vertx.registerVerticleFactory(verticleFactory)

        environment.getProperty("vertx.deploymentVerticle")?.let { deployVerticle(it) }

        logger.info { "Vert.x running!" }
    }

    companion object {
        lateinit var vertx: Vertx
    }
}