package nl.kute.gradle.config.plugin

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class ElasticMqPluginTest {
    @Test
    fun `Using the plugin id should apply the plugin`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("nl.kute.gradle.config.plugin.elasticmq")
        assertThat(project.plugins.getPlugin(ElasticMqPlugin::class.java)).isNotNull
    }

    @Test
    fun `Applying the plugin should register the elasticMq extension`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(ElasticMqPlugin::class.java)
        assertThat(project.elasticmq()).isNotNull
    }
}