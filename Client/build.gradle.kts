import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
//    id("org.jetbrains.kotlin.plugin.sam.with.receiver") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
}


group = "com.adisalagic.codenames.client"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }


    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                val kotlinVersion = "1.7.20"
                kotlin("gradle-plugin", version = kotlinVersion)
                kotlin("serialization", version = kotlinVersion)
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("com.google.code.gson:gson:2.10.1")
                implementation("org.apache.logging.log4j:log4j-api:2.19.0")
                implementation("org.apache.logging.log4j:log4j-core:2.19.0")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            includeAllModules = true
            modules("java.instrument", "java.sql", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "codenames"
            packageVersion = "1.0.0"
            vendor = "AdisAlagic"
            windows {
                this.console = true
                iconFile.set(project.file("CN.ico"))
            }
            linux {
                iconFile.set(project.file("CN.png"))
            }
        }
        buildTypes.release {
            proguard {
                configurationFiles.from("compose-desktop.pro")
            }
        }
    }
}