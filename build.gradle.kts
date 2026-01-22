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