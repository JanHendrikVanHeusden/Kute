package nl.kute.gradle.config.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GUtil

private const val EXTENSION_NAME = "elasticmq"

open class ElasticMqPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(
            EXTENSION_NAME,
            ElasticMqExtension::class.java,
            project)

        project.gradle.buildFinished {
            extension.instances.forEach { serverConfiguration ->
                serverConfiguration.elasticMqInstance.stop()
            }
        }

        extension.instances.forEach { serverConfiguration ->
            val name = serverConfiguration.name.toTaskName()
            project.tasks.register("start${name}ElasticMq", StartElasticMq::class.java, serverConfiguration.name)
            project.tasks.register("stop${name}ElasticMq", StopElasticMq::class.java, serverConfiguration.name)
        }
    }
}

internal fun String.toTaskName() =
    this.toLowerCase()
        .map(::toValidTaskNameCharacters)
        .joinToString(separator = "")
        .toCamelCase()

private fun toValidTaskNameCharacters(char: Char): Char =
    if (char != '_' && Character.isJavaIdentifierPart(char)) { char } else { ' ' }

private fun String.toCamelCase() = GUtil.toCamelCase(this)

internal fun Project.elasticMq(): ElasticMqExtension =
    extensions.getByName(EXTENSION_NAME) as? ElasticMqExtension
        ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")
