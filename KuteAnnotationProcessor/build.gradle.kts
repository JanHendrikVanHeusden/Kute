version = "1.0-SNAPSHOT"

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

sourceSets.test {
    // To tell Gradle not to look in "src/test/java" (for Java classes)
    java.srcDirs("src/test/kotlin")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    gradlePluginPortal()}

plugins {
    // Values retrieved from gradle.properties
    // This seems about the only way; you cannot retrieve them otherwise from outside the plugins' scope
    val kotlinVersion: String by System.getProperties()
    val owaspDependencyCheckVersion: String by System.getProperties()
    val dependencyCheckVersion: String by System.getProperties()
    val dokkaVersion: String by System.getProperties()

    id("java")

    kotlin("jvm") version kotlinVersion

    `java-library`
    `maven-publish`
    id("org.owasp.dependencycheck") version owaspDependencyCheckVersion
    id("com.github.ben-manes.versions") version dependencyCheckVersion
    id("jacoco")
    id("idea")
    id("org.jetbrains.dokka") version dokkaVersion
}

dependencies {
    val jupiterVersion by System.getProperties()
    val kotestRunnerVersion by System.getProperties()
    val mockitoKotlinVersion by System.getProperties()
    val assertJVersion by System.getProperties()

    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.20-1.0.11")
    implementation(project(":KuteCore"))

    // These inherit version from Kotlin version
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestRunnerVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")

    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
}

tasks.test {
    useJUnitPlatform()
}