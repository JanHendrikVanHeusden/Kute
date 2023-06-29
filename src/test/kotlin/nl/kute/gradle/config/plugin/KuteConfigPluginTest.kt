package nl.kute.gradle.config.plugin

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class KuteConfigPluginTest {
    @Test
    fun `using the plugin id should apply the plugin`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("nl.kute.gradle.config.plugin")
        assertThat(project.plugins.getPlugin(KuteConfigPlugin::class.java)).isNotNull
    }
}