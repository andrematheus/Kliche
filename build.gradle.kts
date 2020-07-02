plugins {
    kotlin("jvm") version "1.3.72"
}

group = "dev.ligpo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.undertow", "undertow-core", "2.1.3.Final")
    implementation("org.tomlj", "tomlj", "1.0.0")
    testImplementation(kotlin("test-junit"))
    testImplementation("com.github.kittinunf.result", "result", "3.0.1")
    testImplementation("com.github.kittinunf.fuel", "fuel", "2.2.3")
}

sourceSets {
    main {
        java.srcDir(listOf("main"))
        resources.srcDir(listOf("main/resources"))
    }
    test {
        java.srcDir(listOf("test"))
        resources.srcDir(listOf("test/resources"))
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}