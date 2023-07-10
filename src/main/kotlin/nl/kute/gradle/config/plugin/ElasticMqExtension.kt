package nl.kute.gradle.config.plugin

import org.gradle.api.Project

interface ServerInstanceConfigurations: MutableList<ServerInstanceConfiguration> {

    val project: Project

    fun getByName(name: String): ServerInstanceConfiguration? =
        firstOrNull { it.name == name }

    fun create(name: String): ServerInstanceConfiguration =
        ServerInstanceConfiguration(name, project = project).let {
            this.add(it)
            it
        }
}

abstract class ServerInstanceConfigurationList: ServerInstanceConfigurations, MutableList<ServerInstanceConfiguration> by ArrayList<ServerInstanceConfiguration>()

open class ElasticMqExtension(val project: Project) {

    val instances = object : ServerInstanceConfigurationList() {

        override val project: Project = this@ElasticMqExtension.project

        override fun getByName(name: String): ServerInstanceConfiguration? =
            firstOrNull { it.name == name }

        override fun create(name: String): ServerInstanceConfiguration =
            ServerInstanceConfiguration(name, project = project).let {
                this.add(it)
                it
            }
    }

}