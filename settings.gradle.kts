rootProject.name = "Kute"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://repo.maven.apache.org/maven2/")
        }
        maven {
            url = uri("https://jitpack.io")
        }
        gradlePluginPortal()
        flatDir {
            dirs("libs")
        }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "nl.kute.gradle.config.plugin.elasticmq") {
                useModule("nl.kute:elasticmq-gradle-plugin:${requested.version}")
            }
        }
    }
}

