import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Locale

// TODO: Move to gradle.properties.
//       Example, see README.md of https://github.com/Vorlonsoft/GradleMavenPush
val appName: String = "asstring"
val appPublishedName: String = "Kute asString"
val appDescription: String =
    """Kute asString is basically a `toString()` alternative, but customizable to fit your needs.
        |And with many practice-based, developer-friendly features.""".trimMargin()
val appGroupId: String = "nl.kute"
val appVersion: String = "1.0.0"
val appArtifactId: String = appName

group = appGroupId
version = appVersion
description = appDescription

val generatedApidocsDir: File = File("build/dokka/html")
val apiDocsTargetDir: File = File("docs")

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
    // requires:
    //  * public stuff to explicitly declare `public`
    //  * requires explicit return types for non-private var/val and methods
    explicitApi()
}

sourceSets.main {
    kotlin.srcDirs("src/main/kotlin")
}

sourceSets.test {
    // To tell Gradle not to look in "src/test/java" (for Java classes)
    // Only a few Java classes exist within Kute for testing purposes, to construct Java objects that are extended by Kotlin classes
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
    val kotlinJvmPluginVersion: String by System.getProperties()
    val owaspDependencyCheckVersion: String by System.getProperties()
    val dependencyCheckVersion: String by System.getProperties()
    val dokkaVersion: String by System.getProperties()
    val pitestPluginVersion: String by System.getProperties()
    val koverVersion: String by System.getProperties()

    kotlin("jvm") version kotlinJvmPluginVersion

    `java-library`
    `maven-publish`
    idea

    id("org.owasp.dependencycheck") version owaspDependencyCheckVersion
    id("com.github.ben-manes.versions") version dependencyCheckVersion

    id("info.solidsoft.pitest") version pitestPluginVersion
    id("org.jetbrains.kotlinx.kover") version koverVersion
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
    val commonsTextVersion by System.getProperties()
    val commonsMathVersion by System.getProperties()
    val commonsIoVersion by System.getProperties()
    val gsonVersion by System.getProperties()
    val guavaVersion by System.getProperties()

    // Kotlin
    //  * `api`, so depending projects don't need to add these dependencies themselves
    //  * Inheriting version from Kotlin version
    api("org.jetbrains.kotlin:kotlin-stdlib")
    api("org.jetbrains.kotlin:kotlin-reflect")

    // Used by Gradle tasks
    compileOnly("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
    compileOnly("commons-io:commons-io:$commonsIoVersion")

    // Inheriting version from Kotlin version
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // JUnit test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
    testImplementation("org.junit.platform:junit-platform-suite-api:$junitPlatformVersion")

    // Other test dependencies
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("org.awaitility:awaitility:$awaitilityVersion")
    // must be specified explicitly, otherwise runtime exception on task pitest
    testRuntimeOnly("org.pitest:pitest-junit5-plugin:$pitestJUnit5PluginVersion")

    // Common libraries - to be used in tests only !
    // Do NOT use these in source code, packaged Kute should not rely on ANY external dependency
    testImplementation("org.apache.commons:commons-lang3:$commonsLangVersion")
    testImplementation("org.apache.commons:commons-text:$commonsTextVersion")
    testImplementation("org.apache.commons:commons-math3:$commonsMathVersion")
    testImplementation("com.google.code.gson:gson:$gsonVersion")
    testImplementation("com.google.guava:guava:$guavaVersion")
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

tasks.register("cleanApiDocsBuildDir") {
    group = "documentation"
    doLast {
        FileUtils.deleteDirectory(generatedApidocsDir)
    }
}

tasks.named("dokkaHtml") {
    dependsOn(tasks.named("cleanApiDocsBuildDir"))
}

tasks.register("generateApiDocs") {
    group = "documentation"
    dependsOn(tasks.named("dokkaHtml"))
}

// Tried with a task of type Copy, but it didn't work as desired in case of non-empty directories
// So using custom task with Apache's FileUtils instead, works nicely
tasks.register("copyApiDocs") {
    group = "documentation"
    dependsOn(tasks.named("generateApiDocs"))
    doLast {
        val tmp = "tmp"
        val tmpDir = File(tmp)
        val docsConfig = "_config.yml"
        var copiedConfig = false

        val docsConfigFile = File("${apiDocsTargetDir.path}/$docsConfig")
        if (docsConfigFile.exists()) {
            FileUtils.moveFileToDirectory(docsConfigFile, tmpDir, true)
            copiedConfig = true
        }

        FileUtils.deleteDirectory(apiDocsTargetDir)
        FileUtils.copyDirectory(generatedApidocsDir, apiDocsTargetDir, true)

        if (copiedConfig) {
            FileUtils.moveFileToDirectory(File("$tmp/$docsConfig"), apiDocsTargetDir, true)
        }
    }
}

tasks.register("assembleWithApiDocs") {
    group = "build"
    dependsOn(tasks.named("assemble"))
    dependsOn(tasks.named("copyApiDocs"))
}

tasks.register("buildWithApiDocs") {
    // This is the preferred `build` task, it keeps the API docs up-to-date with the source
    //
    // On Windows you frequently may run into IOExceptions
    //   Probably because Windows writes asynchronously to files, and keeps locks on files
    //   for a second or so after writing or deleting, this may block subsequent access
    // If that is the case, do the following:
    //  1. Instead of `buildWithApiDocs`, run the normal `build` task
    //  2. Manually delete the API docs `kute` folder (see variable `apiDocsDir`)
    //  3. Run the `generateApiDocs` task
    //  4. Manually copy the contents of the generateApiDocs `html` folder (see variable `generatedApidocsDir`)
    //     to the API docs directory
    //  5. Add the files in the `apiDocsDir` folder to git
    group = "build"
    dependsOn(tasks.named("build"))
    dependsOn(tasks.named("copyApiDocs"))
}

pitest {
    // pitest output is not quite satisfactory (probably because it's Kotlin, not Java)
    // On several classes pitest report zero test coverage (0.0);
    // while IntelliJ and kover show 100% for that class

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
            // So no need to publish it on `scans.gradle.com`
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

publishing {
    publications {
        create<MavenPublication>("Kute") {
            groupId = appGroupId
            artifactId = appArtifactId
            version = appVersion
            from(components["java"])

            pom {
                name.set(appPublishedName)
                description.set(appDescription)
            }
        }
    }
    repositories {
        maven {
            name = mavenLocal().name
            url = mavenLocal().url
            // todo: license
        }
    }
}

fun isVersionNonStable(version: String): Boolean {
    val hasStableKeyword = listOf("RELEASE", "FINAL", "GA")
        .any { version.uppercase(Locale.getDefault()).contains(it) }
    val stableVersionPattern = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = hasStableKeyword || stableVersionPattern.matches(version)
    return isStable.not()
}
