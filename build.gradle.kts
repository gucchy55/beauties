plugins {
  application
  id("dev.equo.p2deps") version "1.7.6"
  id("com.github.johnrengelman.shadow") version "8.1.1"
  id("com.github.ben-manes.versions") version "0.51.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
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

p2deps {
  into("implementation") {
    p2repo("https://download.eclipse.org/eclipse/updates/4.30/R-4.30-202312010110/")
    install("org.eclipse.swt.cocoa.macosx.aarch64")
    install("org.eclipse.jface")
  }
}

