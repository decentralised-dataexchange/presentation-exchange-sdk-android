plugins {
    kotlin("jvm") version "1.7.10"
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation(project(":lib"))
}

tasks.test {
    useJUnitPlatform()
}