import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion by extra { "1.2.41" }
val kotlinSerialVersion by extra { "0.5.0" }
val kotlinCoroutineVersion by extra { "0.22.5" }
val okhttpVersion by extra { "3.10.0" }
val moshiVersion by extra { "1.5.0" }
val http4kVersion by extra { "3.26.5" }
val expektVersion by extra { "0.5.0" }
val kotlinPoetVersion by extra { "0.7.0" }
val rxjavaVersion by extra { "2.1.13" }
val rxtestVersion by extra { "1.0.7" }
val dolphinPlatformVersion by extra { "0.18.0" }

plugins {
    val kotlinVersion = "1.2.41"
    val dokkaVersion = "0.9.16"

    kotlin("jvm") version kotlinVersion
    id("org.jetbrains.dokka") version dokkaVersion
}

buildscript {
    val kotlinSerialVersion by extra { "0.5.0" }

    repositories {
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx")
    }

    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-gradle-serialization-plugin:$kotlinSerialVersion")
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

    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutineVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$kotlinCoroutineVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:$kotlinCoroutineVersion")
    compile("io.reactivex.rxjava2:rxjava:$rxjavaVersion")
    compile("com.squareup:kotlinpoet:$kotlinPoetVersion")

    compile("org.http4k:http4k-core:$http4kVersion")
    compile("org.http4k:http4k-client-okhttp:$http4kVersion")
    compile("org.http4k:http4k-format-moshi:$http4kVersion")

    compile("com.canoo.dolphin-platform:dolphin-platform-remoting-server-spring:$dolphinPlatformVersion")
    compile("com.canoo.dolphin-platform:dolphin-platform-remoting-client-javafx:$dolphinPlatformVersion")
}

dependencies {
    testCompile(kotlin("test", kotlinVersion))
    testCompile(kotlin("test-junit", kotlinVersion))

    testCompile("com.winterbe:expekt:$expektVersion")
    testImplementation("com.rubylichtenstein:rxtest:$rxtestVersion")
}

//java {
//    sourceCompatibility = JavaVersion.VERSION_1_8
//    targetCompatibility = JavaVersion.VERSION_1_8
//}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    withType<DokkaTask> {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/javadoc"
    }

    "dokkaJavadoc"(DokkaTask::class) {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/javadoc"
    }

    "wrapper"(Wrapper::class) {
        gradleVersion = "4.7"
        distributionUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-all.zip"
    }
}
