package nl.kute.gradle.config.plugin

import nl.kute.core.annotation.option.defaultNullString
import nl.kute.gradle.config.plugin.property.GradleProperty
import org.gradle.api.Project

class KuteConfiguration(val nullAs: String = defaultNullString, project: Project) {

    var attributes: MutableMap<*, *> by GradleProperty(project, MutableMap::class.java, mutableMapOf<String, String>())

    fun attribute(attribute: String, value: String) {
        @Suppress("UNCHECKED_CAST")
        val map = attributes as? MutableMap<String, String>
        map?.put(attribute, value)
    }

}