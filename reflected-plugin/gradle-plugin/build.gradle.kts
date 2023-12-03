plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("java-gradle-plugin")
    `maven-publish`
}

group = "com.levinzonr.navigation.reflected"
version = "1.0.0"

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "${group}.plugin"
            implementationClass = "NavigationReflectedPlugin"
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:8.0.1")
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.gradle.plugin)
    compileOnly(libs.google.autoservice)
    kapt(libs.google.autoservice)
}

