plugins {
    application
    kotlin("jvm") version "1.4.10"
}

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
    implementation("org.lesscss", "lesscss", "1.7.0.1.1") {
        exclude("org.slf4j", "slf4j-simple")
    }
    implementation("com.github.spullara.mustache.java:compiler:0.9.6")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("org.apache.tika:tika-core:1.18")

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