plugins {
    application
    java
    id("org.danilopianini.gradle-java-qa") version "1.164.0"
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.primus.app.PrimusApp")
}

// Task per installare automaticamente l'hook git
val installGitHooks by tasks.registering(Copy::class) {
    from(File(rootProject.rootDir, "config/git/pre-commit"))
    into(File(rootProject.rootDir, ".git/hooks"))
    filePermissions {
        unix("rwxrwxrwx")
    }
}

// Fai in modo che git hooks vengano installati ogni volta che compili o fai check
tasks.named("check") {
    dependsOn(installGitHooks)
}

tasks.named("compileJava") {
    dependsOn(installGitHooks)
}
dependencies{
    // The BOM (Bill of Materials) synchronizes all the versions of Junit coherently.
    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    // The annotations, assertions and other elements we want to have access to when compiling our tests.
    testImplementation("org.junit.jupiter:junit-jupiter")
    // The engine that must be available at runtime to run the tests.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    //Spotbugs annotations
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.7.3")
    testImplementation("com.github.spotbugs:spotbugs-annotations:4.7.3")
    // slf4j for logging
    implementation("org.slf4j:slf4j-api:2.0.17")
    // logback for logging
    implementation("ch.qos.logback:logback-classic:1.5.28")
}

val test by tasks.getting(Test::class) {
    // Use junit platform for unit tests
    useJUnitPlatform()
    testLogging {
        events(*(org.gradle.api.tasks.testing.logging.TestLogEvent.values())) // events("passed", "skipped", "failed")
    }
    testLogging.showStandardStreams = true
}
