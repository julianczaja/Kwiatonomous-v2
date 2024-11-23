val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val koinVersion: String by project
val exposedVersion: String by project

plugins {
    application
    kotlin("jvm") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    id("io.ktor.plugin") version "3.0.1"
}

group = "com.corrot"
version = "0.2.0"

application {
    mainClass.set("com.corrot.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

tasks {
    shadowJar {
        archiveFileName = "kwiatonomous_backend_$version.jar"
        manifest {
            attributes(Pair("Main-Class", "com.example.ApplicationKt"))
            mergeServiceFiles()
        }
    }
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")

    // GSON
    implementation("com.google.code.gson:gson:2.11.0")

    // Logging
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Test
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")

    // Database
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.47.0.0")

    // DI
    implementation("io.insert-koin:koin-ktor:$koinVersion")

    // Auth
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")

    // FreeMaker
    implementation("io.ktor:ktor-server-freemarker:$ktorVersion")
}