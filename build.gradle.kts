val kotlinVersion by project
val junitJupiterVersion by project
val junitPlatformVersion by project

val okhttpVersion by extra { "3.9.0" }
val moshiVersion by extra { "1.5.0" }
val http4kVersion by extra { "3.0.0" }
val expektVersion by extra { "0.5.0" }

buildscript {
    repositories {
        mavenCentral()
//        maven("https://kotlin.bintray.com/kotlinx")
    }

    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.2")
//        classpath("org.jetbrains.kotlinx:kotlinx-gradle-serialization-plugin:0.2")
    }
}

apply {
    plugin("org.junit.platform.gradle.plugin")
//    plugin("kotlinx-serialization")
}

plugins {
    kotlin("jvm") version "1.2.0"
}

repositories {
    jcenter()
//    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    compile(kotlin("stdlib", "$kotlinVersion"))
    compile(kotlin("reflect", "$kotlinVersion"))
    compile(kotlin("stdlib-jdk7", "$kotlinVersion"))
    compile(kotlin("stdlib-jdk8", "$kotlinVersion"))

//    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.2")
    compile("com.squareup.okhttp3:okhttp:$okhttpVersion")
    compile("com.squareup.moshi:moshi:$moshiVersion")
    compile("com.squareup.moshi:moshi-kotlin:$moshiVersion")

    compile("org.http4k:http4k-core:$http4kVersion")
    compile("org.http4k:http4k-client-okhttp:$http4kVersion")
}

dependencies {
    testCompile(kotlin("test", "$kotlinVersion"))
    testCompile("com.winterbe:expekt:$expektVersion")

    testCompile("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testCompile("org.junit.platform:junit-platform-runner:$junitPlatformVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testRuntime("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
}
