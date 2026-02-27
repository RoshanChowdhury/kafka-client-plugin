plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "org.roshan.kafka"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:3.9.1")
    implementation("io.confluent:kafka-avro-serializer:8.1.1")
    implementation("io.confluent:kafka-protobuf-serializer:8.1.1")
    implementation("io.confluent:kafka-json-schema-serializer:8.1.1")
    implementation("com.google.code.gson:gson:2.11.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

intellij {
    version.set("2024.3.3")
    type.set("IC")
    plugins.set(listOf("com.intellij.java"))
}

tasks {
    patchPluginXml {
        sinceBuild.set("243")
        untilBuild.set("")
    }

    buildSearchableOptions {
        enabled = false
    }
}
