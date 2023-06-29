package nl.kute.gradle.config.plugin

import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

internal typealias QueueConfigurationContainer =
        NamedDomainObjectContainer<QueueConfiguration>

class ServerInstanceConfiguration(
    val name: String,
    project: Project
) {

    // ...

    val queues = project.container(QueueConfiguration::class.java) { name ->
        QueueConfiguration(name, project)
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