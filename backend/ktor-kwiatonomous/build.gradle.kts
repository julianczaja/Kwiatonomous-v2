val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.corrot"
version = "0.0.4"

application {
    mainClass.set("com.corrot.ApplicationKt")
}

repositories {
    mavenCentral()
}

tasks{
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "com.example.ApplicationKt"))
            mergeServiceFiles()
        }
    }
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-jetty:$ktor_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Test
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")

    // Database
    implementation("org.jetbrains.exposed:exposed:0.17.14")
    implementation("org.xerial:sqlite-jdbc:3.36.0.2")
    // DI
    implementation("io.insert-koin:koin-ktor:3.1.4")
}