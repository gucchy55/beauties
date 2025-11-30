import org.gradle.internal.os.OperatingSystem

plugins {
    application
    id("dev.equo.p2deps") version libs.versions.p2deps.get()
    id("com.github.ben-manes.versions") version libs.versions.versions.plugin.get()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get().toInt())
    }
}

repositories {
    mavenCentral()
}

application {
    mainClass.set(libs.versions.mainClass.get())
}

dependencies {
    testImplementation("junit:junit:${libs.versions.junit.get()}")
    implementation("mysql:mysql-connector-java:${libs.versions.mysql.get()}")
}

p2deps {
    into("implementation") {
        p2repo(libs.versions.eclipse.repo.get())
        install(libs.versions.eclipse.swt.get())
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
            "Main-Class" to libs.versions.mainClass.get(),
            "Class-Path" to configurations.runtimeClasspath.get().files
                .joinToString(" ") { "lib/${it.name}" }
        )
    }
}

// ビルド時に依存関係を自動コピー
tasks.build {
    dependsOn(copyDependencies)
}

tasks.named<JavaExec>("run") {
    if (OperatingSystem.current().isMacOsX) {
        jvmArgs = listOf("-XstartOnFirstThread")
    }
}

tasks.wrapper {
    gradleVersion = libs.versions.gradle.get()
    distributionType = Wrapper.DistributionType.ALL
}
