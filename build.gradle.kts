val kotlinVersion by extra { "1.2.20" }
val okhttpVersion by extra { "3.9.0" }
val moshiVersion by extra { "1.5.0" }
val http4kVersion by extra { "3.11.1" }
val expektVersion by extra { "0.5.0" }

plugins {
    kotlin("jvm") version "1.2.20"
}

buildscript {
    repositories {
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx")
    }

    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-gradle-serialization-plugin:0.4")
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

    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.4")
    compile("com.squareup.okhttp3:okhttp:$okhttpVersion")
    compile("com.squareup.moshi:moshi:$moshiVersion")
    compile("com.squareup.moshi:moshi-kotlin:$moshiVersion")

    compile("org.http4k:http4k-core:$http4kVersion")
    compile("org.http4k:http4k-client-okhttp:$http4kVersion")
}

dependencies {
    testCompile(kotlin("test", kotlinVersion))
    testCompile(kotlin("test-junit", kotlinVersion))

    testCompile("com.winterbe:expekt:$expektVersion")
}
