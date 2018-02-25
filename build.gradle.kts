import org.jetbrains.dokka.gradle.DokkaTask

val kotlinVersion by extra { "1.2.21" }
val kotlinSerialVersion by extra { "0.4.1" }
val okhttpVersion by extra { "3.9.1" }
val moshiVersion by extra { "1.5.0" }
val http4kVersion by extra { "3.17.1" }
val expektVersion by extra { "0.5.0" }

plugins {
    kotlin("jvm") version "1.2.21"
    id("org.jetbrains.dokka") version "0.9.16"
}

buildscript {
    repositories {
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx")
    }

    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-gradle-serialization-plugin:0.4.1")
    }
}

apply {
    plugin("kotlinx-serialization")
}

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    compile(kotlin("stdlib", kotlinVersion))
    compile(kotlin("reflect", kotlinVersion))
    compile(kotlin("stdlib-jdk7", kotlinVersion))
    compile(kotlin("stdlib-jdk8", kotlinVersion))

    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$kotlinSerialVersion")
    compile("com.squareup.okhttp3:okhttp:$okhttpVersion")
    compile("com.squareup.moshi:moshi:$moshiVersion")
    compile("com.squareup.moshi:moshi-kotlin:$moshiVersion")

    compile("org.http4k:http4k-core:$http4kVersion")
    compile("org.http4k:http4k-client-okhttp:$http4kVersion")
    compile("org.http4k:http4k-format-moshi:$http4kVersion")

    compile("com.squareup:kotlinpoet:0.6.0")
    compile("io.reactivex.rxjava2:rxjava:2.1.9")

    compile("com.canoo.dolphin-platform:dolphin-platform-remoting-server-spring:0.18.0")
    compile("com.canoo.dolphin-platform:dolphin-platform-remoting-client-javafx:0.18.0")
}

dependencies {
    testCompile(kotlin("test", kotlinVersion))
    testCompile(kotlin("test-junit", kotlinVersion))

    testCompile("com.winterbe:expekt:$expektVersion")
    testImplementation("com.rubylichtenstein:rxtest:1.0.5")
}

tasks {
    "dokkaJavadoc"(DokkaTask::class) {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/javadoc"
    }
}
