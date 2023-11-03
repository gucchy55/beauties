plugins {
  application
  id("dev.equo.p2deps") version "1.7.3"
  id("dev.equo.ide") version "1.7.3"
  id("com.github.johnrengelman.shadow") version "8.1.1"
  id("com.github.ben-manes.versions") version "0.49.0"
}

repositories {
  mavenCentral()
}

application {
    mainClass.set("beauties.main.BeautiesMain")
}

tasks.shadowJar {
    isZip64 = true
}

dependencies {
  testImplementation("junit:junit:4.+")
  implementation("mysql:mysql-connector-java:latest.release")
}

p2deps {
  into("implementation", {
    p2repo("https://download.eclipse.org/eclipse/updates/4.29/")
    install("org.eclipse.equinox.target.categoryIU")
  })
}

equoIde {
  p2repo("https://download.eclipse.org/eclipse/updates/4.29/")
}

