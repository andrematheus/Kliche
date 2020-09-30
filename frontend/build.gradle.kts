plugins {
    kotlin("js") version "1.4.10"
}

kotlin {
    js {
        browser {
        }
    }
    sourceSets {
        main {
            kotlin.srcDir(listOf("main"))
            resources.srcDir(listOf("main/resources"))
        }
        test {
            kotlin.srcDir(listOf("test"))
            resources.srcDir(listOf("test/resources"))
        }
    }
}

repositories {
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-js"))

    testImplementation(kotlin("test-js"))
}