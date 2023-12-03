import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("com.levinzonr.navigation.reflected.plugin") version "1.0.0"
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.hilt)
}


android {
    namespace = "com.levinzonr.composenavigation.reflected"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.levinzonr.composenavigation.reflected"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    implementation(project(":reflected-navigation"))
    implementation(project(":reflected-navigation-material"))
    implementation(libs.google.accompanist.navigation.material)
    implementation(libs.android.core.ktx)
    implementation(libs.android.compose.material3)
    implementation(libs.android.material)
    implementation(libs.android.lifecycle.runtime.ktx)
    implementation(libs.android.activity.compose)
    implementation(libs.google.hilt)
    implementation(libs.google.hilt.compose)
    ksp(libs.google.hilt.compiler)
    implementation(libs.android.navigation.compose)
    implementation(platform(libs.android.compose.bom))
    implementation(libs.bundles.android.compose)
    debugImplementation(libs.android.compose.test.manifest)
}