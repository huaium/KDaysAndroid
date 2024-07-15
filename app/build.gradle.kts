import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

// initialize keystore properties
val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.kdays.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kdays.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.1-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                "String",
                "UC_ADDRESS",
                keystoreProperties.getProperty("UC_ADDRESS")
            )
            buildConfigField(
                "String",
                "UC_KEY",
                keystoreProperties.getProperty("UC_KEY")
            )
            buildConfigField(
                "String",
                "UC_SECRET",
                keystoreProperties.getProperty("UC_SECRET")
            )

            buildConfigField(
                "String",
                "BBS_ADDRESS",
                keystoreProperties.getProperty("BBS_ADDRESS")
            )
            buildConfigField(
                "String",
                "BBS_KEY",
                keystoreProperties.getProperty("BBS_KEY")
            )
            buildConfigField(
                "String",
                "BBS_SECRET",
                keystoreProperties.getProperty("BBS_SECRET")
            )
        }

        debug {
            buildConfigField(
                "String",
                "UC_ADDRESS",
                keystoreProperties.getProperty("UC_ADDRESS_DEBUG")
            )
            buildConfigField(
                "String",
                "UC_KEY",
                keystoreProperties.getProperty("UC_KEY")
            )
            buildConfigField(
                "String",
                "UC_SECRET",
                keystoreProperties.getProperty("UC_SECRET")
            )

            buildConfigField(
                "String",
                "BBS_ADDRESS",
                keystoreProperties.getProperty("BBS_ADDRESS_DEBUG")
            )
            buildConfigField(
                "String",
                "BBS_KEY",
                keystoreProperties.getProperty("BBS_KEY")
            )
            buildConfigField(
                "String",
                "BBS_SECRET",
                keystoreProperties.getProperty("BBS_SECRET")
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Use BoM to monitor compose version
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Material Design 3
    implementation(libs.compose.material3)

    // Android Studio Preview support
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // UI Tests
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)

    // Optional - Add full set of material icons
    implementation(libs.compose.material.icons.extended)
    // Optional - Add window size utils
    implementation(libs.compose.material3.window.size)

    // Optional - Integration with activities
    implementation(libs.activity.compose)
    // Optional - Integration with ViewModels
    implementation(libs.viewmodel.compose)
    // Optional - Integration with LiveData
    implementation(libs.livedata.compose)

    // Addition - Navigation
    implementation(libs.nav.compose)

    // Third-party
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.glide)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.video)
    implementation(libs.picture.selector)
    implementation(libs.ucrop)

    // Custom
    implementation(project(":richeditor-compose"))

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.livedata.ktx)
    implementation(libs.viewmodel.ktx)
    implementation(libs.runtime.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.ui.ktx)
    implementation(libs.recylerview)
    implementation(libs.swiperefreshlayout)
    implementation(libs.crypto)
    implementation(libs.preference.ktx)
    implementation(libs.webkit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso.core)
}