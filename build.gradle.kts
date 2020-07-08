plugins {
    application
    kotlin("jvm") version "1.3.72"
}

group = "dev.ligpo"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("kliche.KlicheKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.undertow", "undertow-core", "2.1.3.Final")
    implementation("org.tomlj", "tomlj", "1.0.0")
    implementation("com.atlassian.commonmark", "commonmark", "0.15.0")
    implementation("de.neuland-bfi", "jade4j", "1.3.2")

    testImplementation("org.junit.jupiter", "junit-jupiter", "5.6.2")
    testImplementation("io.github.rybalkinsd", "kohttp", "0.12.0")
    testImplementation("commons-io", "commons-io", "2.7")
    testImplementation("org.testcontainers", "testcontainers", "1.14.3")
    testImplementation("org.testcontainers", "junit-jupiter", "1.14.3")
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
        useJUnitPlatform {
            excludeTags("slow")
        }
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    register("slowTest", Test::class.java) {
        useJUnitPlatform {
            includeTags("slow")
        }
    }

    "slowTest" {
        shouldRunAfter("test")
    }
    "check" {
        dependsOn("slowTest")
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}

val mainClass = "kliche.KlicheKt"

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes("Main-Class" to mainClass)
        }
        from(configurations.runtimeClasspath.get()
            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
    }
}

tasks {
    "build" {
        dependsOn("fatJar")
    }
    "assemble" {
        dependsOn("fatJar")
    }
}