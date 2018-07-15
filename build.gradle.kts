import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val gradleWrapperVersion by extra { "4.9-rc-2" }
val kotlinVersion by extra { "1.2.51" }
val kotlinSerialVersion by extra { "0.6.0" }
val kotlinCoroutinesVersion by extra { "0.23.4" }
val okhttpVersion by extra { "3.11.0" }
val moshiVersion by extra { "1.6.0" }
val http4kVersion by extra { "3.33.0" }
val expektVersion by extra { "0.5.0" }
val kotlinPoetVersion by extra { "0.7.0" }
val jacksonVersion by extra { "2.9.6" }
val rxjavaVersion by extra { "2.1.16" }
val rxtestVersion by extra { "1.0.7" }
val dolphinPlatformVersion by extra { "0.18.0" }

plugins {
    val kotlinVersion = "1.2.51"
    val dokkaVersion = "0.9.17"

    kotlin("jvm") version kotlinVersion
    id("org.jetbrains.dokka") version dokkaVersion
}

buildscript {
    val kotlinSerialVersion by extra { "0.6.0" }

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
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$kotlinCoroutinesVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:$kotlinCoroutinesVersion")
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
    testCompile(kotlin("test-junit5", kotlinVersion))

    testCompile("io.kotlintest:kotlintest-runner-junit5:3.1.7")
    testCompile("com.winterbe:expekt:$expektVersion")
    testImplementation("com.rubylichtenstein:rxtest:$rxtestVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}

tasks {
    withType<Wrapper> {
        gradleVersion = gradleWrapperVersion
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<Test> {
        useJUnitPlatform()

        testLogging {
            events("PASSED", "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR")
        }
    }

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
}
