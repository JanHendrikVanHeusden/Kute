plugins {
    id("java")
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

dependencies {
    val jupiterVersion by System.getProperties()
    testImplementation(platform("org.junit:junit-bom:$jupiterVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}