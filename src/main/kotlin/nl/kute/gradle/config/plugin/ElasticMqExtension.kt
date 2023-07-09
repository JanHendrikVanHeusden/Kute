package nl.kute.gradle.config.plugin

import org.gradle.api.Project

open class ElasticMqExtension(val project: Project) {
    val instances: MutableList<ServerInstanceConfiguration> = mutableListOf()
}

internal fun Collection<ServerInstanceConfiguration>.getByName(name: String): ServerInstanceConfiguration? =
    this.firstOrNull { it.name == name }