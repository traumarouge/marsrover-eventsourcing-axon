import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.50"
    id("org.springframework.boot") version "2.1.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

version = "0.0.1-SNAPSHOT"

val axonVersion = "4.2"

tasks.withType<JavaCompile> {
    with(options) {
        compilerArgs.add("-Xlint:deprecation")
        compilerArgs.add("-Xlint:unchecked")
        compilerArgs.add("-parameters")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.h2database:h2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.axonframework:axon-spring-boot-starter:$axonVersion") {
        exclude(group = "org.axonframework", module = "axon-server-connector")
    }

    testImplementation("org.axonframework:axon-test:$axonVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
    testImplementation("org.assertj:assertj-core:3.13.2")
    testImplementation("org.hamcrest:hamcrest:2.1")
}