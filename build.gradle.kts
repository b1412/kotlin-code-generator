import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    val kotlinVersion = "1.4.20"
    maven
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version kotlinVersion
}

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    api("com.github.b1412:kotlin-code-generator-meta:11abea967b")

    val arrowVersion = "0.11.0"
    arrow(arrowVersion)

    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    api("org.springframework.boot:spring-boot-starter-freemarker")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-data-jpa")

    api("com.google.guava:guava:29.0-jre")

    testApi("org.junit.jupiter:junit-jupiter-api")
    testApi("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

fun DependencyHandlerScope.arrow(arrowVersion: String) {
    api("io.arrow-kt:arrow-fx:$arrowVersion")
    api("io.arrow-kt:arrow-optics:$arrowVersion")
    api("io.arrow-kt:arrow-syntax:$arrowVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
