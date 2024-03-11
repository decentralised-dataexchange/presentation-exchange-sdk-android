plugins {
    kotlin("jvm") version "1.7.10"
}

group = "org.example"
version = "unspecified"

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation(project(":lib"))
    // JSON serialisation and deserialisation
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.test {
    useJUnitPlatform()
}