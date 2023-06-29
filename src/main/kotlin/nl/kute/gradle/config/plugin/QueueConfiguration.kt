package nl.kute.gradle.config.plugin

import nl.kute.gradle.config.plugin.property.GradleProperty
import org.gradle.api.Project

class QueueConfiguration(
    val name: String,
    project: Project
) {

    init {
        if (name.isBlank()) {
            throw IllegalArgumentException("Name must not be blank nor empty")
        }
    }

    var attributes by GradleProperty(project, MutableMap::class.java, mutableMapOf<String, String>())

    fun attribute(attribute: String, value: String) {
        @Suppress("UNCHECKED_CAST")
        val map = attributes as? MutableMap<String, String>
        map?.put(attribute, value)
    }
}