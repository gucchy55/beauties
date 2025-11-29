plugins {
    application
    id("dev.equo.p2deps") version "1.7.8"
    id("com.github.ben-manes.versions") version "0.52.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
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
        p2repo("https://download.eclipse.org/eclipse/updates/4.36/R-4.36-202505281830/")
        install("org.eclipse.swt.cocoa.macosx.aarch64")
        install("org.eclipse.jface")
    }
}

// 外部jarファイルをlibフォルダにコピーするタスク
val copyDependencies by tasks.registering(Copy::class) {
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("libs/lib"))
    include("*.jar")
}

// jarタスクをカスタマイズ（Class-Pathマニフェストを設定）
tasks.jar {
    dependsOn(copyDependencies)

    manifest {
        attributes(
            "Main-Class" to "beauties.main.BeautiesMain",
            "Class-Path" to configurations.runtimeClasspath.get().files
                .joinToString(" ") { "lib/${it.name}" }
        )
    }
}

// ビルド時に依存関係を自動コピー
tasks.build {
    dependsOn(copyDependencies)
}
