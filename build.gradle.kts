import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.util.Locale

group = "nl.kute"
version = "1.0-SNAPSHOT"
description = "Kute"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

sourceSets.test {
    // To tell Gradle not to look in "src/test/java" (for Java classes)
    java.srcDirs("src/test/kotlin")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform() // JUnit 5
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
}

fun property(name: String) = properties[name] as String

plugins {
    // Values retrieved from gradle.properties
    // This seems about the only way; you cannot retrieve them otherwise from outside the plugins' scope
    val kotlinVersion: String by System.getProperties()
    val owaspDependencyCheckVersion: String by System.getProperties()
    val dependencyCheckVersion: String by System.getProperties()
    val dokkaVersion: String by System.getProperties()

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
    val dokkaVersion by System.getProperties()
    val jupiterVersion by System.getProperties()
    val kotestRunnerVersion by System.getProperties()
    val mockitoKotlinVersion by System.getProperties()
    val assertJVersion by System.getProperties()
    val commonsLangVersion by System.getProperties()

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")

    // Used in tests only.
    // Do not use it in source code, packaged Kute should not rely on any external dependency
    testImplementation("org.apache.commons:commons-lang3:$commonsLangVersion")

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

dependencyCheck {
    formats = listOf("json", "html")
    outputDirectory = "build/reports/owasp"
    failBuildOnCVSS = 8.0F
    failOnError = false
    suppressionFile = "owasp-suppression.xml"
    println("OWASP dependency reports:")
    formats.forEach {
        println("\t$outputDirectory/dependency-check-report.$it")
    }
    println("Suppression file: $suppressionFile")
}

tasks.withType<DependencyUpdatesTask> {
    // In Gradle widget, this task can be found under Tasks -> help
    // (do not confuse with tasks under `owasp dependency-check`
    outputDir = "build/reports/dependencies"
    reportfileName = "dependencies-report" // .txt will be added implicitly
    rejectVersionIf {
        isVersionNonStable(this.candidate.version) && !isVersionNonStable(this.currentVersion)
    }
}


fun isVersionNonStable(version: String): Boolean {
    val hasStableKeyword = listOf("RELEASE", "FINAL", "GA")
        .any { version.uppercase(Locale.getDefault()).contains(it) }
    val stableVersionPattern = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = hasStableKeyword || stableVersionPattern.matches(version)
    return isStable.not()
}
