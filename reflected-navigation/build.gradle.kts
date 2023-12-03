import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.maven.publish)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.levinzon.reflected.navigation"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles.add(File("consumer-rules.pro"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"

    }
}

dependencies {
    implementation(libs.android.core.ktx)
    implementation(libs.android.navigation.compose)
    implementation(project(":reflected-core"))
    api(project(":reflected-core"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.android.test.ext.junit)
    androidTestImplementation(libs.android.espresso.core)
}

mavenPublishing {
    pomFromGradleProperties()
    publishToMavenCentral(SonatypeHost.S01)
    configure(AndroidSingleVariantLibrary("release"))

}