plugins {
    kotlin("jvm") version "1.7.10"
}

group = "org.example"
version = "unspecified"

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation(project(":lib"))
}

tasks.test {
    useJUnitPlatform()
}