plugins {
  application
  id("com.diffplug.eclipse.mavencentral") version "3.33.1"
  id("com.github.johnrengelman.shadow") version "7.0.0"
  id("com.github.ben-manes.versions") version "0.39.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  mavenCentral()
}

application {
    mainClass.set("beauties.main.BeautiesMain")
}

dependencies {
  testImplementation("junit:junit:4.+")
  implementation("mysql:mysql-connector-java:latest.release")
}

eclipseMavenCentral {
    release("4.20.0") {
        implementationNative("org.eclipse.swt")
        implementation("org.eclipse.jface")
        useNativesForRunningPlatform()
    }
}

tasks.wrapper {
  gradleVersion = "7.1"
}
