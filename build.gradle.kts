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