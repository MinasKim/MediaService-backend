import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.spring") version "1.5.0"
    jacoco
}

group = "com.mediaservice"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://dl.bintray.com/kotlin/exposed")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.36.2")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit5", "1.5.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.test {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        html.required.set(false)
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(file("$buildDir/jacoco.html"))
    }

    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/application/dto/**",
                        "**/application/validator/**",
                        "**/domain/**",
                        "**/config/**",
                        "**/web/**",
                        "**/exception/**",
                        "**/infrastructure/**"
                    )
                }
            }
        )
    )
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)

    violationRules {
        rule {}
    }
}

val testCoverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage."

    dependsOn(":test", ":jacocoTestReport", ":jacocoTestCoverageVerification")
    val jacocoTestReport = tasks.findByName("jacocoTestReport")
    jacocoTestReport?.mustRunAfter(tasks.findByName("test"))
    tasks.findByName("jacocoTestCoverageVerification")?.mustRunAfter(jacocoTestReport)
}
