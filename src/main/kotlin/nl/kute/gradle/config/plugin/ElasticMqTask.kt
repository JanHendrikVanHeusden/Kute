package nl.kute.gradle.config.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

sealed class ElasticMqTask(
    private val action: ElasticMqInstance.() -> Unit,
    @Input val serverName: String
) : DefaultTask() {

    init {
        group = "elasticmq"
    }

    @TaskAction
    fun doAction() {
        project.elasticMq().instances.getByName(serverName)?.elasticMqInstance?.action()
    }
}

open class StartElasticMq @Inject constructor(name: String) : ElasticMqTask(ElasticMqInstance::start, name) {

    init {
        description = "Starts the $name ElasticMQ Server Instance, if not running"
    }
}

open class StopElasticMq @Inject constructor(name: String) : ElasticMqTask(ElasticMqInstance::stop, name) {

    init {
        description = "Stops the $name ElasticMQ Server Instance, if running"
    }
}