package com.example.demo.vertx

import com.example.demo.vertx.VertxConfiguration.Companion.vertx
import io.vertx.core.Promise
import io.vertx.core.Verticle
import io.vertx.core.spi.VerticleFactory
import io.vertx.core.spi.VerticleFactory.removePrefix
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.lang.Class.forName
import java.util.concurrent.Callable

@Configuration
@Service
class SpringVerticleFactory: VerticleFactory, ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    override fun createVerticle(
        verticleName: String,
        classLoader: ClassLoader,
        promise: Promise<Callable<Verticle>>
    ) {
        vertx.executeBlocking<Verticle>({
            it.complete(applicationContext.getBean(forName(removePrefix(verticleName))) as Verticle)
        }) {
            when (it.succeeded()) {
                true -> promise.complete(Callable { it.result() })
                false -> promise.fail(it.cause())
            }
        }
    }

    override fun prefix(): String = PREFIX

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    companion object {
        const val PREFIX = "systek-spring-examples"
    }
}