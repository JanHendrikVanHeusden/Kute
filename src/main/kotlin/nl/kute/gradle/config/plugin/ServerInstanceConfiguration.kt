package nl.kute.gradle.config.plugin

import groovy.lang.Closure
import org.elasticmq.rest.sqs.SQSLimits
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

internal typealias QueueConfigurationContainer = NamedDomainObjectContainer<QueueConfiguration>

class ServerInstanceConfiguration(
    val name: String,
    val protocol: String = "http",
    val host: String = "localhost",
    val port: String = "9324",
    val contextPath: String = "path",
    val limits: String = "relaxed",
    project: Project
) {

    // ...

    internal var elasticMqInstance = ElasticMqInstance(project, this)

    internal val sqsLimits
        get() = when (limits) {
            "relaxed" -> SQSLimits.Relaxed()
            "strict" -> SQSLimits.Strict()
            else -> throw IllegalArgumentException(
                "Only 'strict' and 'relaxed' are accepted as limits")
        }

    val queues = project.container(QueueConfiguration::class.java) { name ->
        QueueConfiguration(name, protocol, host, port, contextPath, limits, project)
    }

    fun queues(config: QueueConfigurationContainer.() -> Unit) {
        queues.configure(object : Closure<Unit>(this, this) {
            fun doCall() {
                @Suppress("UNCHECKED_CAST")
                (delegate as? QueueConfigurationContainer)?.let {
                    config(it)
                }
            }
        })
    }


    fun queues(config: Closure<Unit>) {
        queues.configure(config)
    }
}