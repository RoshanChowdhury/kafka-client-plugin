plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "org.roshan.kafka"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:3.9.1")
    implementation("io.confluent:kafka-avro-serializer:8.1.1")
    implementation("io.confluent:kafka-protobuf-serializer:8.1.1")
    implementation("io.confluent:kafka-json-schema-serializer:8.1.1")
    implementation("com.google.code.gson:gson:2.11.0")
    
    // SSL/TLS support
    implementation("org.bouncycastle:bcpkix-jdk18on:1.79")
    implementation("org.bouncycastle:bcprov-jdk18on:1.79")
    
    // OAuth/SASL support
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("com.nimbusds:nimbus-jose-jwt:10.0.2")
    implementation("com.nimbusds:oauth2-oidc-sdk:11.31.1")
    
    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.3")
    testImplementation("org.mockito:mockito-core:5.7.0")
    
    intellijPlatform {
        intellijIdeaCommunity("2024.3.3")
        bundledPlugin("com.intellij.java")
        instrumentationTools()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

intellijPlatform {
    buildSearchableOptions = false
    instrumentCode = true
}

tasks {
    patchPluginXml {
        sinceBuild.set("243")
        untilBuild.set("")
    }
    
    test {
        useJUnit()
    }
}
