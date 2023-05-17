import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.util.Locale

group = "nl.kute"
version = "1.0-SNAPSHOT"
description = "Kute"
java.sourceCompatibility = JavaVersion.VERSION_17

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

plugins {
    // Do not use 1.8.21, it gives this problem:
    //    Unable to find a variant of org.jetbrains.kotlin:kotlin-test:1.8.21 providing
    //    the requested capability org.jetbrains.kotlin:kotlin-test-framework-junit5
    kotlin("jvm") version "1.8.20"

    `java-library`
    `maven-publish`
    id("org.owasp.dependencycheck") version "8.2.1"
    id("com.github.ben-manes.versions") version "0.46.0"
    id("jacoco")
    id("idea")
    id("org.jetbrains.dokka") version "1.8.10"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.8.10")

    // Used in tests only.
    // Do not use it in source code, packaged Kute should not rely on any external dependency
    testImplementation("org.apache.commons:commons-lang3:3.12.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")

    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

sourceSets.test {
    // To tell Gradle does not to look in "src/test/java" (for Java classes)
    java.srcDirs("src/test/kotlin")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
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
