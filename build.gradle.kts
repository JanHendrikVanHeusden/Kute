import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Locale
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.config.ExplicitApiMode

group = "nl.kute"
version = "1.0-SNAPSHOT"
description = "Kute"
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    println("Running on JVM version: ${JavaVersion.current()}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // * the jsr-305=strict setting enforces strict nullability checks
        // * the jvm-default-all argument lets Java classes recognise default methods in Kotlin interfaces
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
        jvmTarget = java.targetCompatibility.toString()
    }
}

kotlin {
    // requires public stuff to explicitly specify `public`; and requires explicit return types for these
    explicitApi()
}

sourceSets.main {
    kotlin.srcDirs("src/main/kotlin")
}

sourceSets.test {
    // To tell Gradle not to look in "src/test/java" (for Java classes)
    // Only a few Java classes exist within Kute for test purposes, to construct Java objects that are extended by Kotlin classes
    java.srcDirs("src/test/kotlin")
    kotlin.srcDirs("src/test/kotlin")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform() // JUnit 5

    // Skip the demo's.
    // These show how `Objects.toString` and Apache's `ToStringBuilder.reflectionToString`
    // crash with stack overflow on recursive stuff (and other issues)
    exclude("**/**Demo.class")
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

plugins {
    // Values retrieved from gradle.properties
    // This seems about the only way; you cannot retrieve them otherwise from outside the plugins' scope
    val kotlinVersion: String by System.getProperties()
    val owaspDependencyCheckVersion: String by System.getProperties()
    val dependencyCheckVersion: String by System.getProperties()
    val dokkaVersion: String by System.getProperties()
    val pitestPluginVersion: String by System.getProperties()

    kotlin("jvm") version kotlinVersion

    `java-library`
    `maven-publish`
    idea

    id("org.owasp.dependencycheck") version owaspDependencyCheckVersion
    id("com.github.ben-manes.versions") version dependencyCheckVersion

    id("jacoco")
    id("info.solidsoft.pitest") version pitestPluginVersion

    id("org.jetbrains.dokka") version dokkaVersion
}

dependencies {
    val dokkaVersion by System.getProperties()
    val junitPlatformVersion by System.getProperties()
    val jupiterVersion by System.getProperties()
    val mockitoKotlinVersion by System.getProperties()
    val assertJVersion by System.getProperties()
    val awaitilityVersion by System.getProperties()
    val pitestJUnit5PluginVersion by System.getProperties()
    val commonsLangVersion by System.getProperties()

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    compileOnly("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")

    // Used in tests only.
    // Do not use it in source code, packaged Kute should not rely on any external dependency
    testImplementation("org.apache.commons:commons-lang3:$commonsLangVersion")

    // These inherit version from Kotlin version
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
    testImplementation("org.junit.platform:junit-platform-suite-api:$junitPlatformVersion")

    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("org.awaitility:awaitility:$awaitilityVersion")

    // must be specified explicitly, otherwise runtime exception on task pitest
    testRuntimeOnly("org.pitest:pitest-junit5-plugin:$pitestJUnit5PluginVersion")
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

tasks.withType<Test> {
    // So test output shows test results (passed, skipped etc.)
    testLogging.showStandardStreams = true
    testLogging.events (
        TestLogEvent.FAILED,
        TestLogEvent.PASSED,
        TestLogEvent.SKIPPED,
        TestLogEvent.STANDARD_OUT
    )
}

pitest {
    // pitest (and jacoco) output not quite satisfactory (maybe because it's Kotlin, not Java?)
    // On several classes pitest and jacoco report zero test coverage (0.0);
    // while IntelliJ shows 100% for that class

    val pitestJUnit5PluginVersion: String by System.getProperties()

    // adds dependency to org.pitest:pitest-junit5-plugin and sets "testPlugin" to "junit5"
    junit5PluginVersion.set(pitestJUnit5PluginVersion)
    avoidCallsTo.set(setOf("kotlin.jvm.internal"))
    mutators.set(setOf("STRONGER"))
    targetClasses.set(setOf("nl.kute.*"))
    targetTests.set(setOf("nl.kute.*Test"))
    threads.set(Runtime.getRuntime().availableProcessors())
    outputFormats.set(setOf("XML", "HTML"))
    // reuse previous executions, to save time
    setProperty("withHistory", true)

    if (hasProperty("buildScan")) {
        extensions.findByName("buildScan")?.withGroovyBuilder {
            setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
            // We just want to run pitest, not to perform a build scan
            // So no need to publishing it on `scans.gradle.com`
            setProperty("termsOfServiceAgree", "no")
        }
    }
}

tasks.named("pitest") {
    doLast {
        println("pitest report: `build/reports/pitest/index.html`")
    }
}

apply(plugin = "info.solidsoft.pitest")

fun isVersionNonStable(version: String): Boolean {
    val hasStableKeyword = listOf("RELEASE", "FINAL", "GA")
        .any { version.uppercase(Locale.getDefault()).contains(it) }
    val stableVersionPattern = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = hasStableKeyword || stableVersionPattern.matches(version)
    return isStable.not()
}
