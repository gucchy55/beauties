plugins {
  // swt deps
  id 'com.diffplug.swt.nativedeps' version '3.27.0'
  id 'com.github.ben-manes.versions' version '0.38.0'
}

repositories {
  jcenter()
}

//////////
// JAVA //
//////////
apply plugin: 'java'

sourceSets {
  main { java {
      srcDir 'src'
  } }
  test { java {
      srcDir 'test'
  } }
}
sourceCompatibility = VER_JAVA
targetCompatibility = VER_JAVA

dependencies {
  compile 'mysql:mysql-connector-java:latest.release'
  testCompile 'junit:junit:4.+'
}

def defaultEncoding = 'UTF-8'
    tasks.withType(AbstractCompile) each { it.options.encoding = defaultEncoding }
    compileTestJava {
        options.encoding = defaultEncoding
    }

task fatJar(type: Jar) {
  group = 'jar'
  baseName = 'beauties'
  from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
  with jar
  manifest {
    attributes(
      'Class-Path': '.',
      'Main-Class': 'beauties.main.BeautiesMain'
      )
  }
  exclude 'META-INF/*.SF'
  exclude 'META-INF/*.DSA'
  exclude 'META-INF/*.RSA'
  exclude 'META-INF/INDEX.LIST'
  exclude 'META-INF/p2.inf'
  exclude 'about_files'
  exclude 'about.html'
  exclude 'chrome.manifest'
  exclude 'fragment.properties'
  exclude 'icons'
  exclude 'plugin.properties'
  exclude 'swt.js'
  exclude 'swt.xpt'
  exclude 'version.txt'
  exclude '.api_description'
}

task copyDependencies(type: Copy) {
   from configurations.compile
   into 'lib'
}

/////////////
// ECLIPSE //
/////////////
apply plugin: 'eclipse'
eclipse {
  project {
    natures 'org.eclipse.jdt.core.javanature'
    natures 'org.eclipse.buildship.core.gradleprojectnature'
    buildCommand 'org.eclipse.jdt.core.javabuilder'
    buildCommand 'org.eclipse.buildship.core.gradleprojectbuilder'
  }
  classpath {
    downloadSources true
    downloadJavadoc true
  }
  jdt {
    sourceCompatibility VER_JAVA
    targetCompatibility VER_JAVA
  }
}
// always create fresh projects
tasks.eclipse.dependsOn(cleanEclipse)
