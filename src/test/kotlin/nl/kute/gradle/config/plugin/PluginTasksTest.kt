package nl.kute.gradle.config.plugin

import com.amazonaws.SdkClientException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("These tests do not work (no tasks created / started). " +
        "No proper source or guide available for ElasticMqExtension in " +
        "https://medium.com/friday-insurance/how-to-write-a-gradle-plugin-in-kotlin-68d7a3534e71"
)
class PluginTasksTest {
    @Test
    fun `Start ElasticMQ Server Task should start a stopped server`() {
        withProject { project, config ->
            project.tasks.withType(StartElasticMq::class.java).single().doAction()

            config.elasticMqInstance.isRunning() shouldBe true
            canConnect(config) shouldBe true
        }
    }

    @Test
    fun `Start ElasticMQ Server Task should belong to the elasticmq group`() {
        val task = project().tasks.withType(StartElasticMq::class.java).single()
        task.group shouldBe "elasticmq"
    }

    @Test
    fun `Start ElasticMQ Server Task should include the instance name in the description`() {
        val task = project().tasks.withType(StartElasticMq::class.java).single()
        task.description.shouldContain("instance-name")
    }

    @Test
    fun `Stop ElasticMQ Server Task should stop a running server`() {
        withProject { project, config ->
            config.elasticMqInstance.start()
            project.tasks.withType(StopElasticMq::class.java).single().doAction()

            config.elasticMqInstance.isRunning() shouldBe false
            canConnect(config) shouldBe false
        }
    }

    @Test
    fun `Stop ElasticMQ Server Task should belong to the elasticmq group`() {
        val task = project().tasks.withType(StopElasticMq::class.java).single()
        task.group shouldBe "elasticmq"
    }

    @Test
    fun `Stop ElasticMQ Server Task should include the instance name in the description`() {
        val task = project().tasks.withType(StopElasticMq::class.java).single()
        task.description.shouldContain("instance-name")
    }

    private fun withProject(test: (Project, ServerInstanceConfiguration) -> Unit) {
        val project = project()
        val config = project.elasticmq().instances.single()
        try {
            test(project, config)
        } finally {
            config.elasticMqInstance.stop()
        }
    }

    private fun project() = ProjectBuilder.builder().build().also { project ->
        project.pluginManager.apply(ElasticMqPlugin::class.java)
        project.elasticmq().instances.create("instance-name")
    }

    private fun canConnect(config: ServerInstanceConfiguration) = try {
        config.elasticMqInstance.createClient().createQueue("queue").queueUrl
        true
    } catch (error: SdkClientException) {
        println(error)
        false
    }
}