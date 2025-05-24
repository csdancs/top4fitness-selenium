plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // Selenium
    testImplementation("org.seleniumhq.selenium:selenium-java:4.20.0")

    // WebDriver manager for auto-download of ChromeDriver, etc.
    testImplementation("io.github.bonigarcia:webdrivermanager:5.8.0")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")

    // YAML
    implementation("org.yaml:snakeyaml:2.2")

    // FAKER
    testImplementation("io.github.serpro69:kotlin-faker:1.13.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(22)
}