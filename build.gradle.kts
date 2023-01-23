import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.7.10"
}

application {
    mainClass.set("MailingListMainKt")
}

group = "com.camcloud"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.http4k:http4k-core:")
    implementation("org.http4k:http4k-contract:4.36.0.0")
    implementation("org.http4k:http4k-format-jackson:4.36.0.0")


    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    runtimeOnly("org.jetbrains.exposed:exposed-jdbc:0.41.1")


    implementation("com.h2database:h2:2.1.214")
    implementation("org.postgresql:postgresql:42.5.1")


    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.slf4j:slf4j-simple:2.0.6")


    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.0.0")
    testImplementation("io.mockk:mockk:1.13.3")
    implementation("org.http4k:http4k-testing-hamkrest:4.37.0.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "18"
}

tasks.jar {
    manifest.attributes["Main-Class"] = "MailingListMainKt"
}


tasks.withType<ShadowJar> {
    archiveFileName.set("app.jar")
}