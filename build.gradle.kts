val kotlinVersion by project
val junitJupiterVersion by project
val junitPlatformVersion by project

plugins {
    kotlin("jvm", "1.1.4-3")
}

repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib", "$kotlinVersion"))
}

dependencies {
    testCompile(kotlin("test", "$kotlinVersion"))
    testCompile("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testRuntime("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
}
