import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform() // JUnit 5
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

sourceSets.test {
    // To tell Gradle not to look in "src/test/java" (for Java classes)
    java.srcDirs("src/test/kotlin")
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

plugins {
    // Values retrieved from gradle.properties
    // This seems about the only way; you cannot retrieve them otherwise from outside the plugins' scope
    val kotlinVersion: String by System.getProperties()
    val dokkaVersion: String by System.getProperties()

    kotlin("jvm") version kotlinVersion

    `java-library`
    `maven-publish`
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
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.20")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestRunnerVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")

    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
}
