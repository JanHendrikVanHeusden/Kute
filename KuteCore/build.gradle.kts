import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

version = "1.0-SNAPSHOT"

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform() // JUnit 5
}

sourceSets.main {
    kotlin.srcDirs("src/main/kotlin")
}

sourceSets.test {
    kotlin.srcDirs("src/main/kotlin")
    // To tell Gradle not to look in "src/test/java" for Java classes
    // (we have a few Java classes there as test objects to verify that functionality also works
    // with Kotlin classes that inherit from Java classes)
    java.srcDirs("src/test/kotlin")
}

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
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestRunnerVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")

    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
}
