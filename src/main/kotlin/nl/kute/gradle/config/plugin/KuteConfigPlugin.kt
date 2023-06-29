package nl.kute.gradle.config.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class KuteConfigPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.task("KuteConfig") {
            it.doLast {
                println("Hello from KuteConfigPlugin")
            }
        }
    }
}
