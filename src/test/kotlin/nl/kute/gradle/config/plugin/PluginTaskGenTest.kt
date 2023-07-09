package nl.kute.gradle.config.plugin

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class PluginTaskGenTest {

    @Test
    fun `Task generation should treat unexpected characters as spaces`() {
        arrayOf(
            '-', '_', '$', 'à', 'á', 'â', 'ã', 'ä', 'ç', 'è', 'é', 'ê', 'ë',
            'ì', 'í', 'î', 'ï', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', 'š', 'ù', 'ú',
            'û', 'ü', 'ý', 'ÿ', 'ž', "\uD83D\uDE01").forEach { c ->

                val project = project(name = "another${c}example")
                project.tasks.withType(ElasticMqTask::class.java).size shouldBe 2
                project.tasks.getByName("startAnotherExampleElasticMq") should beInstanceOf(StartElasticMq::class)
                project.tasks.getByName("stopAnotherExampleElasticMq") should beInstanceOf(StopElasticMq::class)
        }
    }

    private fun project(name: String) = ProjectBuilder.builder().build().also { project ->
        project.pluginManager.apply(ElasticMqPlugin::class.java)
    }
}