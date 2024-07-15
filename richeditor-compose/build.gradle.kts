plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)

                // HTML parsing library
                implementation(libs.ksoup.html)
                implementation(libs.ksoup.entities)

                // Markdown parsing library
                implementation(libs.jetbrains.markdown)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "com.mohamedrejeb.richeditor.compose"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        consumerProguardFile("proguard-rules.pro")
    }

    kotlin {
        jvmToolchain(17)
    }
}