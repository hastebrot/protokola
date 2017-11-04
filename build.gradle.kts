val kotlinVersion by project
val junitJupiterVersion by project
val junitPlatformVersion by project

buildscript {
    repositories {
        mavenCentral()
//        maven("https://kotlin.bintray.com/kotlinx")
    }

    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.1")
//        classpath("org.jetbrains.kotlinx:kotlinx-gradle-serialization-plugin:0.2")
    }
}

apply {
    plugin("org.junit.platform.gradle.plugin")
//    plugin("kotlinx-serialization")
}

plugins {
    kotlin("jvm") version "1.1.51"
}

repositories {
    jcenter()
//    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    compile(kotlin("stdlib", "$kotlinVersion"))
//    compile(kotlin("reflect", "$kotlinVersion"))
//    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.2")
    compile("com.squareup.okhttp3:okhttp:3.9.0")
    compile("com.squareup.moshi:moshi:1.5.0")
    compile("com.squareup.moshi:moshi-kotlin:1.5.0")
}

dependencies {
    testCompile(kotlin("test", "$kotlinVersion"))
    testCompile("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testCompile("org.junit.platform:junit-platform-runner:$junitPlatformVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testRuntime("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
}
