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
    implementation("com.atlassian.commonmark", "commonmark", "0.15.0")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.6.2")
    testImplementation("io.github.rybalkinsd", "kohttp", "0.12.0")
    testImplementation("commons-io", "commons-io", "2.7")
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
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}