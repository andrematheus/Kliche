plugins {
    kotlin("js") version "1.4.10"
}

repositories {
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
    jcenter()
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

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains:kotlin-react:16.13.1-pre.105-kotlin-1.3.72")
    implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.105-kotlin-1.3.72")
    implementation(npm("react", "16.13.1"))
    implementation(npm("react-dom", "16.13.1"))

    testImplementation(kotlin("test-js"))
}