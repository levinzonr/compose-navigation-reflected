import com.vanniktech.maven.publish.GradlePublishPlugin
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("java-gradle-plugin")
    alias(libs.plugins.maven.publish)
}

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "com.levinzonr.reflected"
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


mavenPublishing {
    pomFromGradleProperties()
    publishToMavenCentral(SonatypeHost.S01)
    configure(GradlePublishPlugin())
}